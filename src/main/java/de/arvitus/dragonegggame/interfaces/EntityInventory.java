package de.arvitus.dragonegggame.interfaces;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public interface EntityInventory {
    SimpleContainer dragonEggGame$getInventory();

    Vec3 dragonEggGame$getPos();

    Level dragonEggGame$getWorld();
}
