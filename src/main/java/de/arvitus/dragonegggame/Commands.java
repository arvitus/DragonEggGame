package de.arvitus.dragonegggame;

import com.mojang.brigadier.context.CommandContext;
import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.config.Config;
import de.arvitus.dragonegggame.config.Data;
import de.arvitus.dragonegggame.config.MessageString;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.net.URI;

import static de.arvitus.dragonegggame.DragonEggGame.CONFIG;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                literal(DragonEggGame.MOD_ID_ALIAS)
                    .requires(Permissions.require(Perms.ADMIN, 4))
                    .then(
                        literal("reload")
                            .requires(Permissions.require(Perms.RELOAD, 4))
                            .executes(Commands::reload)
                    )
                    .executes(context -> {
                        FabricLoader.getInstance().getModContainer(DragonEggGame.MOD_ID).ifPresent(modContainer -> {
                            ModMetadata meta = modContainer.getMetadata();
                            context.getSource().sendFeedback(
                                () ->
                                    Text.of(
                                        String.format(
                                            "%s v%s by %s",
                                            meta.getName(),
                                            meta.getVersion(),
                                            meta.getAuthors().stream().findFirst().isEmpty()
                                                ? "Unknown"
                                                : meta.getAuthors().stream().findFirst().get().getName()
                                        )
                                    ).copy().setStyle(
                                        Style.EMPTY
                                            .withClickEvent(new ClickEvent.OpenUrl(URI.create(meta
                                                .getContact()
                                                .get("source")
                                                .orElse("https://github.com/arvitus")
                                            )))
                                            .withHoverEvent(new HoverEvent.ShowText(
                                                Text.of("Click to view source")
                                            ))),
                                false
                            );
                        });
                        return 0;
                    })
            );

            dispatcher.register(
                literal("dragon_egg")
                    .requires(Permissions.require(Perms.BASE, true))
                    .then(
                        literal("bearer")
                            .requires(Permissions.require(Perms.BEARER, true))
                            .executes(Commands::dragon_egg$bearer)
                    )
                    .then(
                        literal("info")
                            .requires(Permissions.require(Perms.INFO, true))
                            .executes(Commands::dragon_egg$info)
                    )
            );
        });
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        Config oldConfig = CONFIG;
        CONFIG = Config.loadOrCreate();
        if (CONFIG == oldConfig) {
            context.getSource().sendError(Text.of(
                "Failed to load config, using previous value instead. See console for more information."));
            return -1;
        }
        DragonEggAPI.init();
        context.getSource()
            .sendFeedback(() -> Text.of("Reloaded DragonEggGame config and data"), false);
        return 1;
    }

    private static int dragon_egg$bearer(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        Data data = DragonEggAPI.getData();
        if (data == null) {
            source.sendError(CONFIG.messages.bearerError.node.toText(PlaceholderContext.of(source.withLevel(4))));
            return -1;
        }

        MessageString message;
        if (data.playerUUID == null) message = CONFIG.messages.noBearer;
        else message = switch (CONFIG.getVisibility(data.type)) {
            case EXACT -> CONFIG.messages.bearerExact;
            case RANDOMIZED -> CONFIG.messages.bearerRandomized;
            case HIDDEN -> CONFIG.messages.bearerHidden;
        };

        TextNode node = message.node;
        source.sendFeedback(() -> node.toText(PlaceholderContext.of(source.withLevel(4))), false);
        return 0;
    }

    private static int dragon_egg$info(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(
            () -> CONFIG.messages.info.node.toText(PlaceholderContext.of(context.getSource().withLevel(4))),
            false
        );
        return 0;
    }
}