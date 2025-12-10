package de.arvitus.dragonegggame;

import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.config.Data;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;

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
            if (BEARER != null && !data.playerUUID.equals(BEARER)) announceChange(data.playerUUID);
            BEARER = data.playerUUID;
        } else BEARER = UUID.randomUUID();
    }

    public static void announceChange(UUID newBearer) {
        Optional.ofNullable(DragonEggGame.server).ifPresent(server -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(newBearer);
            if (player == null) return;
            server.getPlayerManager().broadcast(
                CONFIG.messages.bearerChanged.node.toText(
                    PlaceholderContext.of(player
                        .getCommandSource()
                        .withAdditionalPermissions(LeveledPermissionPredicate.OWNERS))
                ),
                false
            );
            server.getPlayerManager().sendToAll(
                new PlaySoundS2CPacket(
                    Registries.SOUND_EVENT.getEntry(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP),
                    SoundCategory.MASTER,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    .5f,
                    1f,
                    Random.create().nextLong()
                )
            );
        });
    }
}
