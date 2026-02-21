package de.arvitus.dragonegggame.mixin;

import de.arvitus.dragonegggame.interfaces.BlockInventory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Copied from
 * <a href="https://github.com/QuiltServerTools/Ledger/blob/master/src/main/java/com/github/quiltservertools/ledger/mixin/LockableContainerBlockEntityMixin.java">ledger</a>
 */
@Mixin(BaseContainerBlockEntity.class)
public abstract class BaseContainerBlockEntityMixin extends BlockEntity implements BlockInventory {
    public BaseContainerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    protected abstract NonNullList<ItemStack> getItems();

    @NotNull
    @Override
    public BlockPos dragonEggGame$getPos() {
        return this.worldPosition;
    }

    @Override
    public @Nullable Level dragonEggGame$getWorld() {
        return this.level;
    }

    @Override
    public NonNullList<ItemStack> dragonEggGame$getInventory() {
        return this.getItems();
    }
}