package de.arvitus.dragonegggame.mixin;

import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.utils.Utils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
    @Inject(
        method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;" +
                 "Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;",
        at = @At(value = "INVOKE", target = "net/minecraft/inventory/Inventory.markDirty()V", shift = At.Shift.AFTER)
    )
    private static void transfer(
        @Nullable Inventory from,
        Inventory to,
        ItemStack stack,
        int slot,
        @Nullable Direction side,
        CallbackInfoReturnable<ItemStack> cir
    ) {
        if (Utils.isOrHasDragonEgg(to.getStack(slot)) && to instanceof BlockEntity blockEntity) {
            DragonEggAPI.updatePosition(
                DragonEggAPI.PositionType.INVENTORY,
                blockEntity.getPos(),
                blockEntity.getWorld()
            );
        }
    }
}
