package de.arvitus.dragonegggame.api;

import com.mojang.authlib.GameProfile;
import de.arvitus.dragonegggame.DragonEggGame;
import de.arvitus.dragonegggame.config.Data;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class APIUtils {
    public static Component getBearerName() {
        MinecraftServer server = DragonEggGame.server;
        Data data = DragonEggAPI.getData();
        if (server == null || data == null || data.playerUUID == null) return Component.literal("Invalid");

        return Component.literal(gameProfileFromUUID(server, data.playerUUID).name());

    }

    public static GameProfile gameProfileFromUUID(@NotNull MinecraftServer server, @NotNull UUID uuid) {
        var player = server.getPlayerList().getPlayer(uuid);
        if (player != null) return player.getGameProfile();

        var cachedUser = server.services().nameToIdCache().get(uuid);
        if (cachedUser.isPresent()) return new GameProfile(uuid, cachedUser.get().name());

        var profile = server.services().profileResolver().fetchById(uuid);
        return profile.orElseGet(() -> new GameProfile(uuid, "Unknown"));
    }
}
