package de.arvitus.dragonegggame;

import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.config.Data;
import de.arvitus.dragonegggame.utils.ScheduledEvent;
import de.arvitus.dragonegggame.utils.Utils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

import static de.arvitus.dragonegggame.DragonEggGame.LOGGER;

public class Events {
    public static final LinkedHashMap<UUID, ScheduledEvent> SCHEDULED_ACTIONS = new LinkedHashMap<>();

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            DragonEggGame.server = server;
            DragonEggAPI.init();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
            Optional.ofNullable(DragonEggAPI.getData()).ifPresent(Data::save)
        );

        ServerTickEvents.END_SERVER_TICK.register(server ->
            new LinkedHashMap<>(SCHEDULED_ACTIONS).forEach((key, value) -> {
                try {
                    if (value.tryRun(server)) SCHEDULED_ACTIONS.remove(key, value);
                } catch (Exception e) {
                    LOGGER.error("Error in scheduled callback", e);
                    SCHEDULED_ACTIONS.remove(key);
                }
            })
        );

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, lifecycledResourceManager, b) -> Commands.reload());

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (player.isCreative() && state.isOf(Blocks.DRAGON_EGG)) DragonEggAPI.clearPosition();
        });

        ServerEntityEvents.EQUIPMENT_CHANGE.register((livingEntity, equipmentSlot, previousStack, currentStack) -> {
            if (Utils.isOrHasDragonEgg(currentStack)) DragonEggAPI.updatePosition(livingEntity);
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            // item entity load cannot be here because /give is weird
            if (!(entity instanceof ItemEntity) && Utils.hasDragonEgg(entity)) DragonEggAPI.updatePosition(entity);
        });

        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (
                entity instanceof FallingBlockEntity fallingBlock &&
                entity.getRemovalReason() == Entity.RemovalReason.KILLED &&
                fallingBlock.getBlockState().isOf(Blocks.DRAGON_EGG)
            ) {
                fallingBlock.dropItem(world, fallingBlock.getBlockState().getBlock());
            }

            /*
            if (
                entity instanceof VehicleInventory inventory &&
                !entity.isRemoved() &&
                inventory.getLootTable() == null &&
                inventory.getInventory().stream().anyMatch(Utils::isOrHasDragonEgg)
            ) {
                int count = 0;
                for (int i = 0; i < inventory.getInventory().size(); i++) {
                    ItemStack stack = inventory.getInventoryStack(i);
                    if (!Utils.isOrHasDragonEgg(stack)) continue;
                    if (stack.isOf(Items.DRAGON_EGG)) count += stack.copyAndEmpty().getCount();
                    else count += Utils.removeDragonEgg(stack).getCount();
                }
                Utils.spawnDragonEggAtSpawn(world.getServer(), count);
            }
             */

            if (entity instanceof ItemEntity item && Utils.isOrHasDragonEgg(item.getStack())) {
                ItemStack stack = item.getStack();
                int count = stack.getCount();
                if (!item.isRemoved()) {
                    if (Utils.isNearServerSpawn(item)) return;
                    if (stack.isOf(Items.DRAGON_EGG)) {
                        item.setDespawnImmediately();
                        item.setStack(Items.STONE.getDefaultStack());
                    }
                    item.setGlowing(false);
                } else if (!item.isGlowing()) return;

                if (!stack.isOf(Items.DRAGON_EGG)) count = Utils.countDragonEgg(stack);
                Utils.spawnDragonEggAtSpawn(world.getServer(), count);
            }
        });

        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
            if (blockEntity instanceof LootableContainerBlockEntity lootableContainerBlockEntity &&
                lootableContainerBlockEntity.getLootTable() != null) return;

            if (blockEntity instanceof LockableContainerBlockEntity containerBlockEntity) {
                MinecraftServer server = world.getServer();
                server.send(new ServerTask(
                    server.getTicks(), () -> {
                    if (containerBlockEntity.isRemoved() || containerBlockEntity.isEmpty()) return;
                    for (int i = 0; i < containerBlockEntity.size(); i++) {
                        ItemStack stack = containerBlockEntity.getStack(i);
                        if (stack.isEmpty()) continue;
                        if (Utils.isOrHasDragonEgg(stack)) {
                            DragonEggAPI.updatePosition(containerBlockEntity);
                            break;
                        }
                    }
                }
                ));
            }
        });
    }
}
