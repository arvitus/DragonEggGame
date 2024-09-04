package de.arvitus.dragonegggame.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.DragonEggBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DragonEggBlock.class)
public abstract class DragonEggBlockMixin {
    /** Fixes <a href="https://bugs.mojang.com/browse/MC-174759">MC-174759</a> */
    @Redirect(
        method = "teleport",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/world/border/WorldBorder.contains(Lnet/minecraft/util/math/BlockPos;)Z"
        )
    )
    private boolean fixVoidTeleport(
        WorldBorder worldBorder,
        BlockPos pos,
        @Local(ordinal = 1) BlockPos blockPos,
        @Local(argsOnly = true) World world
    ) {
        return worldBorder.contains(blockPos) && !world.isOutOfHeightLimit(blockPos);
    }
}
