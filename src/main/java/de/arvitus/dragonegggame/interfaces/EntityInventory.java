package de.arvitus.dragonegggame.interfaces;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface EntityInventory {
    SimpleInventory dragonEggGame$getInventory();

    Vec3d dragonEggGame$getPos();

    World dragonEggGame$getWorld();
}
