package de.arvitus.dragonegggame.api;

import de.arvitus.dragonegggame.Events;
import de.arvitus.dragonegggame.config.Config;
import de.arvitus.dragonegggame.config.Data;
import de.arvitus.dragonegggame.utils.ScheduledEvent;
import de.arvitus.dragonegggame.utils.Utils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

import static de.arvitus.dragonegggame.DragonEggGame.*;


public class DragonEggAPI {
    private static final LinkedHashSet<Consumer<@NotNull Data>> onUpdateConsumers = new LinkedHashSet<>();
    private static final List<DeferredUpdate> deferredUpdates = new ArrayList<>();
    private static @Nullable Data data;

    public static void init() {
        Data oldData = data;
        load_data();
        if (Objects.equals(oldData, data)) return;
        dispatchUpdate();
    }

    private static void load_data() {
        data = Data.load();
        if (server != null) {
            data.world = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, Identifier.of(data.worldId)));
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
        data = new Data();
        data.save();
        dispatchUpdate();
    }

    public static void updatePosition(@NotNull Entity entity) {
        updatePosition(getPositionType(entity), entity.getEntityPos(), entity.getEntityWorld(), entity);
    }

    public static void updatePosition(@NotNull BlockEntity blockEntity) {
        updatePosition(PositionType.INVENTORY, blockEntity.getPos(), Objects.requireNonNull(blockEntity.getWorld()));
    }

    public static void updatePosition(@NotNull BlockPos pos, @NotNull World world) {
        updatePosition(PositionType.BLOCK, pos, world);
    }

    public static void updatePosition(@NotNull PositionType type, @NotNull BlockPos pos, @NotNull World world) {
        updatePosition(type, pos.toCenterPos(), world, null);
    }

    private static synchronized void updatePosition(
        @NotNull PositionType type,
        @NotNull Vec3d pos,
        @NotNull World world,
        @Nullable Entity entity
    ) {
        if (data == null) {
            LOGGER.warn("API not ready, deferring position update");
            deferredUpdates.add(new DeferredUpdate(type, pos, world, entity));
            return;
        }

        if (entity != null) trackEntity(entity);
        if (type == data.type && pos.distanceTo(data.getPosition()) < 0.00001) return;

        devLogger(
            "Updating Dragon Egg position to type: {}, pos: {}, world: {}, entity: {}",
            type,
            BlockPos.ofFloored(pos).toShortString(),
            world.getRegistryKey().getValue(),
            entity
        );


        data.entityUUID = entity != null ? entity.getUuid() : null;
        if (entity instanceof ServerPlayerEntity player && !Objects.equals(data.playerUUID, player.getUuid())) {
            data.durations = new Data.Durations();
            data.playerUUID = player.getUuid();
        }

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
        data.setPosition(pos);

        data.save();
        dispatchUpdate();
    }

    private static synchronized void trackEntity(Entity entity) {
        if (CONFIG.getVisibility(getPositionType(entity)) == Config.VisibilityType.EXACT) entity.setGlowing(true);
        Events.SCHEDULED_ACTIONS.put(
            entity.getUuid(), new ScheduledEvent(
                100,
                server -> Optional.ofNullable(DragonEggAPI.getData()).ifPresent(data -> {
                    if (entity.isRemoved()) return;
                    entity.setGlowing(false);
                    if (!Utils.hasDragonEgg(entity)) return;
                    DragonEggAPI.updatePosition(entity);
                })
            )
        );
    }

    public static @Nullable Data getData() {
        return data;
    }

    public static PositionType getPositionType(@NotNull Entity entity) {
        return switch (entity) {
            case ItemEntity ignored -> PositionType.ITEM;
            case FallingBlockEntity ignored -> PositionType.FALLING_BLOCK;
            case PlayerEntity ignored -> PositionType.PLAYER;
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
    }

    private record DeferredUpdate(PositionType type, Vec3d pos, World world, Entity entity) {}
}
