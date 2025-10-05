package de.arvitus.dragonegggame.mixin;

import de.arvitus.dragonegggame.interfaces.EntityInventory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractHorseEntity.class)
public abstract class AbstractHorseEntityMixin extends AnimalEntity implements EntityInventory {
    @Shadow
    protected SimpleInventory items;

    protected AbstractHorseEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public SimpleInventory dragonEggGame$getInventory() {
        return this.items;
    }

    @Override
    public Vec3d dragonEggGame$getPos() {
        return this.getEntityPos();
    }

    @Override
    public World dragonEggGame$getWorld() {
        return this.getEntityWorld();
    }
}
