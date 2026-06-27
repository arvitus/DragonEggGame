package de.arvitus.dragonegggame.utils;

import de.arvitus.dragonegggame.interfaces.BlockInventory;
import de.arvitus.dragonegggame.interfaces.EntityInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {
    /**
     * Generates a random position around the given position with the given radius
     *
     * @param pos    The center position
     * @param radius The radius around the center position
     * @return The generated position
     */
    public static BlockPos randomizePosition(BlockPos pos, float radius) {
        return pos.offset(randomSpherePoint(radius));
    }

    /**
     * Generates a random point inside a sphere with the given radius with the center at (0, 0, 0)
     *
     * @param radius The radius of the sphere
     * @return The generated point
     */
    private static BlockPos randomSpherePoint(float radius) {
        double u = Math.random();
        double v = Math.random();
        double theta = 2 * Math.PI * u;
        double phi = Math.acos(2 * v - 1);
        double r = Math.cbrt(Math.random()) * radius;
        double x = r * Math.sin(phi) * Math.cos(theta);
        double y = r * Math.sin(phi) * Math.sin(theta);
        double z = r * Math.cos(phi);
        return BlockPos.containing(x, y, z);
    }

    /**
     * Checks if the given ItemStack is a dragon egg or contains a dragon egg.
     * This method is recursive and will check all nested ItemStacks up to a depth of 10.
     * So a dragon egg inside a bundle inside a shulker box inside a chest will still be detected.
     *
     * @param stack The ItemStack to check
     * @return True if the ItemStack is a dragon egg or contains a dragon egg, otherwise false
     */
    public static boolean isOrHasDragonEgg(ItemStack stack) {
        return countDragonEgg(stack) > 0;
    }

    /**
     * Counts the number of dragon eggs in the given ItemStack.
     * This method is recursive and will check all nested ItemStacks up to a depth of 10.
     * So a dragon egg inside a bundle inside a shulker box inside a chest will still be detected.
     *
     * @param stack The ItemStack to count the dragon eggs in
     * @return The number of dragon eggs in the ItemStack
     */
    public static int countDragonEgg(ItemStack stack) {
        return countDragonEgg(stack, 0);
    }

    private static int countDragonEgg(ItemStack stack, int currentDepth) {
        if (stack.isEmpty() || currentDepth >= 10) return 0;
        if (stack.is(Items.DRAGON_EGG)) return stack.getCount();

        DataComponentMap components = stack.getComponents();
        if (components == DataComponentMap.EMPTY) return 0;

        AtomicInteger count = new AtomicInteger();

        Optional
            .ofNullable(components.get(DataComponents.CONTAINER))
            .ifPresent(containerComponent -> containerComponent
                .nonEmptyItems()
                .forEach(template -> count.addAndGet(countDragonEgg(template.create(), currentDepth + 1))));
        Optional
            .ofNullable(components.get(DataComponents.BUNDLE_CONTENTS))
            .ifPresent(bundleComponent -> bundleComponent
                .items()
                .forEach(template -> count.addAndGet(countDragonEgg(template.create(), currentDepth + 1))));

        return count.get();
    }

    /**
     * Spawns the given number of dragon eggs at the world spawn position
     *
     * @param server The MinecraftServer instance
     * @param count  The number of dragon eggs to spawn
     * @return The spawned ItemEntity or null if the entity could not be spawned
     */
    public static @Nullable ItemEntity spawnDragonEggAtSpawn(@NotNull MinecraftServer server, int count) {
        ServerLevel overworld = server.overworld();
        Vec3 spawnPos = Vec3.atCenterOf(overworld.getRespawnData().pos());
        ItemEntity itemCopy = new ItemEntity(
            overworld,
            spawnPos.x,
            spawnPos.y,
            spawnPos.z,
            Items.DRAGON_EGG.getDefaultInstance().copyWithCount(count)
        );
        if (overworld.addFreshEntity(itemCopy)) return itemCopy;
        return null;
    }

    /**
     * Checks if the given entity is near the server spawn (overworld) position
     *
     * @param entity The entity to check
     * @return True if the entity is near the server spawn position, otherwise false
     */
    public static boolean isNearServerSpawn(Entity entity) {
        ServerLevel overworld = Objects.requireNonNull(entity.level().getServer()).overworld();
        return entity.level() == overworld && entity.position().closerThan(
            Vec3.atCenterOf(overworld.getRespawnData().pos()), 3
        );
    }

    public static boolean hasDragonEgg(Entity entity) {
        if (entity instanceof LivingEntity livingEntity && livingEntity.isHolding(Utils::isOrHasDragonEgg)) return true;
        return switch (entity) {
            // non-living
            case ItemEntity item -> Utils.isOrHasDragonEgg(item.getItem());
            case FallingBlockEntity fallingBlock -> fallingBlock.getBlockState().is(Blocks.DRAGON_EGG) || (
                fallingBlock.blockData != null &&
                hasDragonEgg(
                    BlockEntity.loadStatic(
                        fallingBlock.blockPosition(),
                        fallingBlock.getBlockState(),
                        fallingBlock.blockData,
                        fallingBlock.level().registryAccess()
                    )
                )
            );
            // e.g. boats and minecarts
            case Container inventory -> inventory.hasAnyMatching(Utils::isOrHasDragonEgg);

            // living
            case Player player -> player.getInventory().contains(Utils::isOrHasDragonEgg);
            case InventoryCarrier owner -> owner.getInventory().hasAnyMatching(Utils::isOrHasDragonEgg);
            case EntityInventory inv -> inv.dragonEggGame$getInventory().hasAnyMatching(Utils::isOrHasDragonEgg);
            case null, default -> false;
        };
    }

    public static boolean hasDragonEgg(BlockEntity blockEntity) {
        return switch (blockEntity) {
            case BlockInventory inventory ->
                inventory.dragonEggGame$getInventory().stream().anyMatch(Utils::isOrHasDragonEgg);
            case null, default -> false;
        };
    }
}
