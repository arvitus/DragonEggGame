package de.arvitus.dragonegggame.utils;

import de.arvitus.dragonegggame.interfaces.BlockInventory;
import de.arvitus.dragonegggame.interfaces.EntityInventory;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
        return pos.add(randomSpherePoint(radius));
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
        return BlockPos.ofFloored(x, y, z);
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
        if (stack.isOf(Items.DRAGON_EGG)) return stack.getCount();

        ComponentMap components = stack.getComponents();
        if (components == ComponentMap.EMPTY) return 0;

        AtomicInteger count = new AtomicInteger();

        Optional
            .ofNullable(components.get(DataComponentTypes.CONTAINER))
            .ifPresent(containerComponent -> containerComponent
                .iterateNonEmpty()
                .forEach(itemStack -> count.addAndGet(countDragonEgg(itemStack, currentDepth + 1))));
        Optional
            .ofNullable(components.get(DataComponentTypes.BUNDLE_CONTENTS))
            .ifPresent(bundleComponent -> bundleComponent
                .iterate()
                .forEach(itemStack -> count.addAndGet(countDragonEgg(itemStack, currentDepth + 1))));

        return count.get();
    }

    /**
     * Removes a dragon egg from the given container ItemStack e.g. a shulker box or a bundle.
     * This method is recursive and will check all nested ItemStacks up to a depth of 10.
     * So a dragon egg inside a bundle inside a shulker box item will still be detected.
     *
     * @param containerStack The ItemStack to remove the dragon egg from
     * @return The removed dragon egg ItemStack or null if no dragon egg was found
     */
    public static ItemStack removeDragonEgg(ItemStack containerStack) {
        int count = 0;
        if (!containerStack.isOf(Items.DRAGON_EGG)) {
            count = countDragonEgg(containerStack, 0);
            removeDragonEgg(containerStack, 0);
        }
        return Items.DRAGON_EGG.getDefaultStack().copyWithCount(count);
    }

    private static void removeDragonEgg(ItemStack containerStack, int currentDepth) {
        if (containerStack.isEmpty() || currentDepth >= 10) return;

        Optional
            .ofNullable(containerStack.get(DataComponentTypes.CONTAINER))
            .ifPresent(containerComponent -> containerComponent.iterateNonEmpty().forEach(itemStack -> {
                if (itemStack.isOf(Items.DRAGON_EGG)) itemStack.setCount(0);
                else removeDragonEgg(itemStack, currentDepth + 1);
            }));

        Optional
            .ofNullable(containerStack.get(DataComponentTypes.BUNDLE_CONTENTS))
            .ifPresent(bundleComponent -> {
                BundleContentsComponent.Builder builder =
                    new BundleContentsComponent.Builder(BundleContentsComponent.DEFAULT);
                bundleComponent.iterate().forEach(itemStack -> {
                    if (!itemStack.isEmpty() && !itemStack.isOf(Items.DRAGON_EGG)) {
                        removeDragonEgg(itemStack, currentDepth + 1);
                        builder.add(itemStack);
                    }
                });
                containerStack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
            });
    }

    /**
     * Spawns the given number of dragon eggs at the world spawn position
     *
     * @param server The MinecraftServer instance
     * @param count  The number of dragon eggs to spawn
     * @return The spawned ItemEntity or null if the entity could not be spawned
     */
    public static @Nullable ItemEntity spawnDragonEggAtSpawn(@NotNull MinecraftServer server, int count) {
        ServerWorld overworld = server.getOverworld();
        Vec3d spawnPos = overworld.getSpawnPos().toCenterPos();
        ItemEntity itemCopy = new ItemEntity(
            overworld,
            spawnPos.x,
            spawnPos.y,
            spawnPos.z,
            Items.DRAGON_EGG.getDefaultStack().copyWithCount(count)
        );
        if (overworld.spawnEntity(itemCopy)) return itemCopy;
        return null;
    }

    /**
     * Checks if the given entity is near the server spawn (overworld) position
     *
     * @param entity The entity to check
     * @return True if the entity is near the server spawn position, otherwise false
     */
    public static boolean isNearServerSpawn(Entity entity) {
        ServerWorld overworld = Objects.requireNonNull(entity.getServer()).getOverworld();
        return entity.getWorld() == overworld && entity.getPos().isInRange(overworld.getSpawnPos().toCenterPos(), 3);
    }

    public static boolean hasDragonEgg(Entity entity) {
        if (entity instanceof LivingEntity livingEntity && livingEntity.isHolding(Utils::isOrHasDragonEgg)) return true;
        return switch (entity) {
            // non-living
            case ItemEntity item -> Utils.isOrHasDragonEgg(item.getStack());
            case FallingBlockEntity fallingBlock -> fallingBlock.getBlockState().isOf(Blocks.DRAGON_EGG) || (
                fallingBlock.blockEntityData != null &&
                hasDragonEgg(
                    BlockEntity.createFromNbt(
                        fallingBlock.getBlockPos(),
                        fallingBlock.getBlockState(),
                        fallingBlock.blockEntityData,
                        fallingBlock.getWorld().getRegistryManager()
                    )
                )
            );
            // e.g. boats and minecarts
            case Inventory inventory -> inventory.containsAny(Utils::isOrHasDragonEgg);

            // living
            case PlayerEntity player -> player.getInventory().contains(Utils::isOrHasDragonEgg);
            case InventoryOwner owner -> owner.getInventory().containsAny(Utils::isOrHasDragonEgg);
            case EntityInventory inv -> inv.dragonEggGame$getInventory().containsAny(Utils::isOrHasDragonEgg);
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
