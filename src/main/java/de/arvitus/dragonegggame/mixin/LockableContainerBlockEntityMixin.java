package de.arvitus.dragonegggame.mixin;

import de.arvitus.dragonegggame.interfaces.BlockInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Copied from
 * <a href="https://github.com/QuiltServerTools/Ledger/blob/master/src/main/java/com/github/quiltservertools/ledger/mixin/LockableContainerBlockEntityMixin.java">ledger</a>
 */
@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerBlockEntityMixin extends BlockEntity implements BlockInventory {
    public LockableContainerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    protected abstract DefaultedList<ItemStack> getHeldStacks();

    @NotNull
    @Override
    public BlockPos dragonEggGame$getPos() {
        return this.pos;
    }

    @Override
    public @Nullable World dragonEggGame$getWorld() {
        return this.world;
    }

    @Override
    public DefaultedList<ItemStack> dragonEggGame$getInventory() {
        return this.getHeldStacks();
    }
}