package de.arvitus.dragonegggame.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface BlockInventory {
    NonNullList<ItemStack> dragonEggGame$getInventory();

    BlockPos dragonEggGame$getPos();

    Level dragonEggGame$getWorld();
}
