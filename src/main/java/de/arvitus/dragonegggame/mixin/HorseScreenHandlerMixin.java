package de.arvitus.dragonegggame.mixin;

import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.utils.Utils;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.HorseScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseScreenHandler.class)
public abstract class HorseScreenHandlerMixin {
    @Shadow
    @Final
    private AbstractHorseEntity entity;

    @Inject(method = "onClosed", at = @At("HEAD"))
    public void onClosed(PlayerEntity player, CallbackInfo ci) {
        if (Utils.hasDragonEgg(this.entity)) DragonEggAPI.updatePosition(this.entity);
    }
}
