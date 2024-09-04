package de.arvitus.dragonegggame.mixin;

import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.interfaces.BlockInventory;
import de.arvitus.dragonegggame.interfaces.DoubleInventoryHelper;
import de.arvitus.dragonegggame.utils.Utils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Inspired from
 * <a href="https://github.com/QuiltServerTools/Ledger/blob/master/src/main/java/com/github/quiltservertools/ledger/mixin/SlotMixin.java">ledger</a>
 */
@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow
    @Final
    public Inventory inventory;
    @Shadow
    @Final
    private int index;

    @Inject(method = "setStack(Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
    private void onStackChange(ItemStack stack, CallbackInfo ci) {
        if (Utils.isOrHasDragonEgg(stack)) {
            if (this.inventory instanceof PlayerInventory playerInventory) {
                DragonEggAPI.updatePosition(playerInventory.player);
                return;
            }

            BlockInventory inventory = this.getBlockInventory();
            if (inventory == null) return;
            World world = inventory.dragonEggGame$getWorld();
            if (world == null) return;

            BlockPos pos = inventory.dragonEggGame$getPos();
            if (this.inventory instanceof BlockEntity)
                DragonEggAPI.updatePosition(DragonEggAPI.PositionType.INVENTORY, pos, world);
            else if (this.inventory instanceof Entity entity)
                DragonEggAPI.updatePosition(entity);
        }

    }

    @Unique
    @Nullable
    private BlockInventory getBlockInventory() {
        Inventory slotInventory = this.inventory;
        if (slotInventory instanceof DoubleInventoryHelper doubleInventoryHelper) {
            slotInventory = doubleInventoryHelper.dragonEggGame$getInventory(this.index);
        }
        if (slotInventory instanceof BlockInventory blockInventory) {
            return blockInventory;
        }

        return null;
    }
}
