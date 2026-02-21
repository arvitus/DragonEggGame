package de.arvitus.dragonegggame;

import com.mojang.brigadier.context.CommandContext;
import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.config.Config;
import de.arvitus.dragonegggame.config.Data;
import de.arvitus.dragonegggame.config.MessageString;
import de.arvitus.dragonegggame.features.Actions;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.net.URI;

import static de.arvitus.dragonegggame.DragonEggGame.CONFIG;
import static de.arvitus.dragonegggame.DragonEggGame.LOGGER;
import static net.minecraft.commands.Commands.literal;

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
                            context.getSource().sendSuccess(
                                () ->
                                    Component.literal(
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
                                                .orElse("https://github.com/arvitus/DragonEggGame")
                                            )))
                                            .withHoverEvent(new HoverEvent.ShowText(
                                                Component.literal("Click to view source")
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

    private static int reload(CommandContext<CommandSourceStack> context) {
        if (!reload()) {
            context.getSource().sendFailure(Component.literal(
                "Failed to load config, using previous value instead. See console for more information."));
            return -1;
        }
        context.getSource()
            .sendSuccess(() -> Component.literal("Reloaded DragonEggGame config and data"), false);
        return 1;
    }

    public static boolean reload() {
        Config oldConfig = CONFIG;
        CONFIG = Config.loadAndUpdateOrCreate();
        if (CONFIG == oldConfig) {
            LOGGER.error("Failed to load config, using previous value instead.");
            return false;
        }
        Actions.register();
        DragonEggAPI.init();
        LOGGER.info("Reloaded DragonEggGame config and data");
        return true;
    }

    private static int dragon_egg$bearer(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Data data = DragonEggAPI.getData();
        if (data == null) {
            source.sendFailure(CONFIG.messages.bearerError.node.toText(PlaceholderContext.of(source.withPermission(4))));
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
        source.sendSuccess(() -> node.toText(PlaceholderContext.of(source.withPermission(4))), false);
        return 0;
    }

    private static int dragon_egg$info(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
            () -> CONFIG.messages.info.node.toText(PlaceholderContext.of(context.getSource().withPermission(4))),
            false
        );
        return 0;
    }
}