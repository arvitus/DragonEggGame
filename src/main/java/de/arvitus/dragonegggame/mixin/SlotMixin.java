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
 * Inspired by
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
        if (!Utils.isOrHasDragonEgg(stack)) return;

        switch (this.inventory) {
            case PlayerInventory playerInventory -> DragonEggAPI.updatePosition(playerInventory.player);
            case BlockEntity blockEntity -> DragonEggAPI.updatePosition(blockEntity);
            case Entity entity -> DragonEggAPI.updatePosition(entity);
            default -> {
                BlockInventory blockInventory = this.getBlockInventory();
                if (blockInventory == null) return;
                BlockPos pos = blockInventory.dragonEggGame$getPos();
                World world = blockInventory.dragonEggGame$getWorld();
                if (pos == null || world == null) return;
                DragonEggAPI.updatePosition(DragonEggAPI.PositionType.INVENTORY, pos, world);
            }
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
