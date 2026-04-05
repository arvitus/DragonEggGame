package de.arvitus.dragonegggame.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Inspired by
 * <a href="https://github.com/QuiltServerTools/Ledger/blob/master/src/main/java/com/github/quiltservertools/ledger/mixin/LockableContainerBlockEntityMixin.java">ledger</a>
 */
@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory {
    @Shadow
    @Final
    public PlayerEntity player;

    @WrapMethod(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z")
    private boolean onItemInsertion(int i, ItemStack itemStack, Operation<Boolean> original) {
        var stack = itemStack.copy();
        var success = original.call(i, itemStack);
        if (success && Utils.isOrHasDragonEgg(stack)) DragonEggAPI.updatePosition(this.player);
        return success;
    }
}