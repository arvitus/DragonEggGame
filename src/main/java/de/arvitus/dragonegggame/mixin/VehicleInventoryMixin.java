package de.arvitus.dragonegggame.mixin;

import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VehicleInventory.class)
public interface VehicleInventoryMixin {
    @Inject(method = "setInventoryStack", at = @At("HEAD"))
    default void onSetInventoryStack(int slot, ItemStack stack, CallbackInfo ci) {
        if (this instanceof Entity entity && Utils.isOrHasDragonEgg(stack)) DragonEggAPI.updatePosition(entity);
    }
}
