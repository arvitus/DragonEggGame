package de.arvitus.dragonegggame.api;

import com.mojang.authlib.GameProfile;
import de.arvitus.dragonegggame.DragonEggGame;
import de.arvitus.dragonegggame.config.Data;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;

import static de.arvitus.dragonegggame.DragonEggGame.LOGGER;

public class APIUtils {
    public static Text getBearer() {
        MinecraftServer server = DragonEggGame.server;
        Data data = DragonEggAPI.getData();
        if (server == null || data == null || data.playerUUID == null) return Text.of("Invalid");

        Text bearer;
        ServerPlayerEntity player;
        UserCache userCache;
        if ((player = server.getPlayerManager().getPlayer(data.playerUUID)) != null)
            bearer = player.getStyledDisplayName();
        else if ((userCache = (UserCache) server.getApiServices().nameToIdCache()) != null &&
                 userCache.getByUuid(data.playerUUID).isPresent())
            bearer = Text.of(userCache.getByUuid(data.playerUUID).get().name());
        else {
            try {
                LOGGER.warn("Falling back to api call to fetch player data. This can impact server performance!");
                GameProfile profile = server
                    .getApiServices()
                    .profileResolver()
                    .getProfileById(data.playerUUID)
                    .orElseThrow();
                bearer = Text.of(profile.name());
            } catch (Exception e) {
                bearer = Text.of("Unknown");
            }
        }
        return bearer;
    }
}
