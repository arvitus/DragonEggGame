package de.arvitus.dragonegggame.mixin;

import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Inspired by
 * <a href="https://github.com/QuiltServerTools/Ledger/blob/master/src/main/java/com/github/quiltservertools/ledger/mixin/LockableContainerBlockEntityMixin.java">ledger</a>
 */
@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory {
    @Shadow
    @Final
    public PlayerEntity player;

    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("RETURN"))
    private void onItemInsertion(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = stack.copyWithCount(cir.getReturnValue() ? 1 : 0);
        if (Utils.isOrHasDragonEgg(itemStack)) DragonEggAPI.updatePosition(this.player);
    }
}