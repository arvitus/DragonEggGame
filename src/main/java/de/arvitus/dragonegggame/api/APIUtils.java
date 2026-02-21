package de.arvitus.dragonegggame.api;

import com.mojang.authlib.GameProfile;
import de.arvitus.dragonegggame.DragonEggGame;
import de.arvitus.dragonegggame.config.Data;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.CachedUserNameToIdResolver;

import static de.arvitus.dragonegggame.DragonEggGame.LOGGER;

public class APIUtils {
    public static Component getBearer() {
        MinecraftServer server = DragonEggGame.server;
        Data data = DragonEggAPI.getData();
        if (server == null || data == null || data.playerUUID == null) return Component.nullToEmpty("Invalid");

        Component bearer;
        ServerPlayer player;
        CachedUserNameToIdResolver userCache;
        if ((player = server.getPlayerList().getPlayer(data.playerUUID)) != null)
            bearer = player.getFeedbackDisplayName();
        else if ((userCache = (CachedUserNameToIdResolver) server.services().nameToIdCache()) != null &&
                 userCache.get(data.playerUUID).isPresent())
            bearer = Component.literal(userCache.get(data.playerUUID).get().name());
        else {
            try {
                LOGGER.warn("Falling back to api call to fetch player data. This can impact server performance!");
                GameProfile profile = server
                    .services()
                    .profileResolver()
                    .fetchById(data.playerUUID)
                    .orElseThrow();
                bearer = Component.literal(profile.name());
            } catch (Exception e) {
                bearer = Component.literal("Unknown");
            }
        }
        return bearer;
    }
}
