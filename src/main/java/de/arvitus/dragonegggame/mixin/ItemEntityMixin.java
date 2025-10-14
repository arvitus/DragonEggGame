package de.arvitus.dragonegggame.mixin;

import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow
    private int itemAge;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    /*
    // Replaced with PlayerInventory.insertStack(ItemStack) to also detect things like /give
    @Inject(
        method = "onPlayerCollision",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;increaseStat(Lnet/minecraft/stat/Stat;I)V"
        )
    )
    private void onPlayerCollision(PlayerEntity player, CallbackInfo ci, @Local ItemStack itemStack) {
        if (itemStack.isOf(Items.DRAGON_EGG)) DragonEggAPI.updatePosition(player);
    }
    */

    @Shadow
    public abstract void setNeverDespawn();

    @Inject(method = "tick", at = @At("HEAD"))
    private void beforeTick(CallbackInfo ci) {
        if (this.getWorld().isClient) return;

        ItemStack stack = this.getStack();
        if (!this.isRemoved() && this.itemAge == 0 && Utils.isOrHasDragonEgg(stack)) {
            this.setGlowing(true);

            if (stack.isOf(Items.DRAGON_EGG) && Utils.isNearServerSpawn(this)) {
                this.setNeverDespawn();
                this.setInvulnerable(true);
            }

            DragonEggAPI.updatePosition(this);
        }
    }
}
