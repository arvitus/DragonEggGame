package de.arvitus.dragonegggame.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "tickInVoid", at = @At("HEAD"))
    private void dropItemContentsInVoid(CallbackInfo ci) {
        if ((Object) this instanceof ItemEntity item) item.getStack().onItemEntityDestroyed(item);
    }
}