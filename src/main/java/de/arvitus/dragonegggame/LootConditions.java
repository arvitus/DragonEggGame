package de.arvitus.dragonegggame;

import com.mojang.serialization.MapCodec;
import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.config.Data;
import net.minecraft.entity.Entity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static de.arvitus.dragonegggame.DragonEggGame.CONFIG;
import static de.arvitus.dragonegggame.DragonEggGame.MOD_ID;

public class LootConditions {
    public static final LootConditionType IS_BEARER = register("is_bearer", IsBearer.CODEC);
    public static final LootConditionType IS_NEARBY = register("is_nearby", IsNearby.CODEC);

    public static void register() {}

    private static LootConditionType register(String id, MapCodec<? extends LootCondition> codec) {
        return Registry.register(
            Registries.LOOT_CONDITION_TYPE,
            Identifier.of(MOD_ID, id),
            new LootConditionType(codec)
        );
    }

    public static class IsBearer implements LootCondition {
        public static final MapCodec<IsBearer> CODEC = MapCodec.unit(new IsBearer());

        @Override
        public LootConditionType getType() {
            return LootConditions.IS_BEARER;
        }

        @Override
        public boolean test(LootContext context) {
            Entity entity = context.get(LootContextParameters.THIS_ENTITY);
            Data data = DragonEggAPI.getData();
            if (entity instanceof ServerPlayerEntity player && data != null)
                return player.getUuid().equals(data.playerUUID);
            return false;
        }
    }

    public static class IsNearby implements LootCondition {
        public static final MapCodec<IsNearby> CODEC = MapCodec.unit(new IsNearby());

        @Override
        public LootConditionType getType() {
            return LootConditions.IS_NEARBY;
        }

        @Override
        public boolean test(LootContext context) {
            Entity entity = context.get(LootContextParameters.THIS_ENTITY);
            Data data = DragonEggAPI.getData();
            if (entity != null && data != null && data.world != null)
                return entity.getEntityPos().isInRange(data.getPosition(), CONFIG.nearbyRange);
            return false;
        }
    }
}


