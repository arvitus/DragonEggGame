package de.arvitus.dragonegggame.mixin;

import de.arvitus.dragonegggame.interfaces.DoubleInventoryHelper;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Copied from
 * <a href="https://github.com/QuiltServerTools/Ledger/blob/master/src/main/java/com/github/quiltservertools/ledger/mixin/DoubleInventoryMixin.java">ledger</a>
 */
@Mixin(DoubleInventory.class)
public abstract class DoubleInventoryMixin implements DoubleInventoryHelper {
    @Shadow
    @Final
    private Inventory first;

    @Shadow
    @Final
    private Inventory second;

    @NotNull
    @Override
    public Inventory dragonEggGame$getInventory(int slot) {
        return slot >= this.first.size() ? this.second : this.first;
    }
}