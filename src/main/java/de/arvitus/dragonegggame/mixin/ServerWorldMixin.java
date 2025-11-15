package de.arvitus.dragonegggame.mixin;

import de.arvitus.dragonegggame.api.DragonEggAPI;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(
        MutableWorldProperties properties,
        RegistryKey<World> registryRef,
        DynamicRegistryManager registryManager,
        RegistryEntry<DimensionType> dimensionEntry,
        boolean isClient,
        boolean debugWorld,
        long biomeAccess,
        int maxChainedNeighborUpdates
    ) {
        super(
            properties,
            registryRef,
            registryManager,
            dimensionEntry,
            isClient,
            debugWorld,
            biomeAccess,
            maxChainedNeighborUpdates
        );
    }

    @Inject(method = "onBlockStateChanged", at = @At("HEAD"))
    public void onBlockStateChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo ci) {
        // Block entities are not yet loaded at this point
        if (newBlock.isOf(Blocks.DRAGON_EGG)) DragonEggAPI.updatePosition(pos, this);
    }
}
