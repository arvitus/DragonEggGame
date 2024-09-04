package de.arvitus.dragonegggame.api;

import de.arvitus.dragonegggame.DragonEggGame;
import de.arvitus.dragonegggame.Events;
import de.arvitus.dragonegggame.config.Config;
import de.arvitus.dragonegggame.files.Data;
import de.arvitus.dragonegggame.utils.ScheduledEvent;
import de.arvitus.dragonegggame.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static de.arvitus.dragonegggame.DragonEggGame.CONFIG;
import static de.arvitus.dragonegggame.DragonEggGame.LOGGER;


public class DragonEggAPI {
    private static final LinkedHashSet<Consumer<@NotNull Data>> onUpdateConsumers = new LinkedHashSet<>();
    private static final List<DeferredUpdate> deferredUpdates = new ArrayList<>();
    private static @Nullable Data data;

    public static void init() {
        load_data();
        dispatchUpdate();
    }

    private static void load_data() {
        data = Data.load();
        if (DragonEggGame.server != null) {
            data.world = DragonEggGame.server.getWorld(RegistryKey.of(RegistryKeys.WORLD, Identifier.of(data.worldId)));
            if (data.world == null) {
                LOGGER.warn("Could not find world with id '{}'", data.worldId);
            }
        }
    }

    public static synchronized void onUpdate(Consumer<@NotNull Data> consumer) {
        onUpdateConsumers.add(consumer);
        if (data != null) consumer.accept(data);
    }

    public static synchronized boolean unregisterListener(Consumer<@NotNull Data> consumer) {
        return onUpdateConsumers.remove(consumer);
    }

    private static void dispatchUpdate() {
        if (data != null) {
            List<DeferredUpdate> updatesToProcess = new ArrayList<>(deferredUpdates);
            deferredUpdates.clear();
            for (DeferredUpdate update : updatesToProcess) {
                updatePosition(update.type, update.pos, update.world, update.entity);
            }

            for (Consumer<@NotNull Data> listener : new ArrayList<>(onUpdateConsumers)) {
                try {
                    listener.accept(data);
                } catch (Exception e) {
                    LOGGER.warn("Error while dispatching update to listener {}: {}", listener, e.getStackTrace());
                }
            }
        } else LOGGER.warn("API is not ready, data is missing");
    }

    public static void clearPosition() {
        updatePosition(PositionType.NONE, null, null);
    }

    public static void updatePosition(@NotNull DragonEggAPI.PositionType type, @Nullable BlockPos pos, World world) {
        Vec3d position = pos == null ? null : pos.toCenterPos();
        updatePosition(type, position, world, null);
    }

    public static void updatePosition(Entity entity) {
        updatePosition(getPositionType(entity), entity.getPos(), entity.getWorld(), entity);
    }

    private static synchronized void updatePosition(
        @NotNull DragonEggAPI.PositionType type,
        Vec3d pos,
        World world,
        @Nullable Entity entity
    ) {
        if (data == null) {
            LOGGER.warn("API not ready, deferring position update");
            deferredUpdates.add(new DeferredUpdate(type, pos, world, entity));
            return;
        }

        LOGGER.info(
            "Updating Dragon Egg position to type: {}, pos: {}, world: {}, entity: {}",
            type,
            pos != null ? BlockPos.ofFloored(pos).toShortString() : null,
            world != null ? world.getRegistryKey().getValue() : null,
            entity
        );

        if (type == PositionType.NONE || pos == null || world == null) {
            data.type = PositionType.NONE;
            data.save();
            dispatchUpdate();
            return;
        }

        if (entity != null) {
            data.entityUUID = entity.getUuid();
            if (type == PositionType.PLAYER) {
                data.playerUUID = entity.getUuid();
            }
            trackEntity(entity);
        } else data.entityUUID = null;

        World oldWorld = data.world != null ? data.world : world;
        if (
            !oldWorld.equals(world) ||
            !pos.isInRange(data.getRandomizedPosition().toCenterPos(), CONFIG.searchRadius)
        ) {
            data.clearRandomizedPosition();
        }

        data.type = type;
        data.world = world;
        data.worldId = world.getRegistryKey().getValue().toString();
        data.position = pos;

        data.save();
        dispatchUpdate();
    }

    private static synchronized void trackEntity(Entity entity) {
        if (CONFIG.getVisibility(getPositionType(entity)) == Config.VisibilityType.EXACT) entity.setGlowing(true);
        Events.SCHEDULED_ACTIONS.put(entity.getUuid(), new ScheduledEvent(
            100,
            server -> Optional.ofNullable(DragonEggAPI.getData()).ifPresent(data -> {
                if (entity.isRemoved()) return;
                entity.setGlowing(false);
                if (!Utils.hasDragonEgg(entity)) return;
                if (entity.getPos() != data.position) DragonEggAPI.updatePosition(entity);
                else trackEntity(entity);
            })
        ));
    }

    public static @Nullable Data getData() {
        return data;
    }

    public static PositionType getPositionType(Entity entity) {
        return switch (entity) {
            case ItemEntity ignored -> PositionType.ITEM;
            case FallingBlockEntity ignored -> PositionType.FALLING_BLOCK;
            case PlayerEntity ignored -> PositionType.PLAYER;
            case null -> PositionType.NONE;
            default -> PositionType.ENTITY;
        };
    }

    public enum PositionType {
        /** The Dragon Egg is a Block */
        BLOCK,
        /** The Dragon Egg is an Item Entity */
        ITEM,
        /** The Dragon Egg is a Falling Block Entity */
        FALLING_BLOCK,
        /** The Dragon Egg Item is part of a block inventory */
        INVENTORY,
        /** A non-player entity is carrying the Dragon Egg */
        ENTITY,
        /** A player is carrying the Dragon Egg */
        PLAYER,
        /** The Dragon Egg does not exist */
        NONE
    }

    private record DeferredUpdate(PositionType type, Vec3d pos, World world, Entity entity) {}
}
