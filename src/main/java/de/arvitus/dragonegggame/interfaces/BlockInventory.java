package de.arvitus.dragonegggame.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockInventory {
    DefaultedList<ItemStack> dragonEggGame$getInventory();

    BlockPos dragonEggGame$getPos();

    World dragonEggGame$getWorld();
}
