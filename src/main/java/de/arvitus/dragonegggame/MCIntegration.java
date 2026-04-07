package de.arvitus.dragonegggame;

import de.arvitus.dragonegggame.api.APIUtils;
import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.api.Event;
import de.arvitus.dragonegggame.config.Data;
import de.arvitus.dragonegggame.features.Actions;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static de.arvitus.dragonegggame.DragonEggGame.CONFIG;

public class MCIntegration {
    private static UUID BEARER;

    public static void init() {
        DragonEggAPI.onUpdate(MCIntegration::onUpdate);
    }

    public static void onUpdate(Data data) {
        if (data.playerUUID != null) {
            if (!data.playerUUID.equals(BEARER)) announceChange(data.playerUUID);
            BEARER = data.playerUUID;
        } else BEARER = null;
    }

    public static void announceChange(UUID newBearer) {
        Optional.ofNullable(DragonEggGame.server).ifPresent(server -> {
            ServerPlayer player = server.getPlayerList().getPlayer(newBearer);
            if (player == null) return;
            server.getPlayerList().broadcastSystemMessage(
                CONFIG.messages.bearerChanged.node.toText(
                    PlaceholderContext.of(player
                        .createCommandSourceStack()
                        .withMaximumPermission(LevelBasedPermissionSet.OWNER))
                ),
                false
            );
            server.getPlayerList().broadcastAll(
                new ClientboundSoundPacket(
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EXPERIENCE_ORB_PICKUP),
                    SoundSource.MASTER,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    .5f,
                    1f,
                    RandomSource.create().nextLong()
                )
            );

            var data = DragonEggAPI.getData();
            if (data == null || BEARER == null) return;

            var placeholders = new HashMap<>(Actions.placeholders);
            placeholders.put("old_bearer_id", () -> BEARER.toString());
            placeholders.put("old_bearer", () -> APIUtils.gameProfileFromUUID(server, BEARER).name());

            Actions.emitEvent("bearer_changed", new Event<>(Actions.getVariables(data), placeholders, null));
        });
    }
}
