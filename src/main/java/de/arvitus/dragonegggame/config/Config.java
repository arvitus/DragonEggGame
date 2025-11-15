package de.arvitus.dragonegggame.config;

import de.arvitus.dragonegggame.api.DragonEggAPI.PositionType;
import eu.pb4.placeholders.api.parsers.NodeParser;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.nio.file.Path;
import java.util.Map;

import static de.arvitus.dragonegggame.DragonEggGame.*;

@ConfigSerializable
public class Config {
    public static final Map<PositionType, VisibilityType> defaultVisibility = Map.of(
        PositionType.BLOCK, VisibilityType.RANDOMIZED,
        PositionType.ITEM, VisibilityType.EXACT,
        PositionType.FALLING_BLOCK, VisibilityType.EXACT,
        PositionType.INVENTORY, VisibilityType.EXACT,
        PositionType.ENTITY, VisibilityType.EXACT,
        PositionType.PLAYER, VisibilityType.HIDDEN
    );
    private static final Path PATH = CONFIG_DIR.resolve("config.conf");
    private static final HoconConfigurationLoader LOADER = HoconConfigurationLoader.builder()
        .path(PATH)
        .prettyPrinting(true)
        .defaultOptions(opts -> opts.serializers(build ->
            build.register(MessageString.class, new MessageString.Serializer(Messages.PARSER))
        ))
        .build();

    @Comment("The radius that is used to randomize the dragon egg position.\nDefault: 25")
    public float searchRadius = 25;
    @Comment("The name of the BlueMap marker")
    public String markerName = "Dragon Egg";
    @Comment("The description of the BlueMap area marker")
    public String areaMarkerDescription = "The dragon egg is somewhere in this area.";
    @Comment("The description of the BlueMap point marker")
    public String pointMarkerDescription = "Come and get it!";
    @Comment("The URL of the BlueMap point marker icon")
    public String pointMarkerIcon = "https://minecraft.wiki/images/thumb/Dragon_Egg_JE4.png/150px-Dragon_Egg_JE4.png";
    @Comment("The color of the BlueMap marker as a decimal value.\nDefault: 2818132 (Purple)")
    public int markerColor = 0x2b0054; // Purple
    @Comment("Messages used throughout the mod")
    public Messages messages = new Messages();
    @Comment(
        """
            The visibility of the dragon egg for each position type.
            Default: {
               BLOCK=RANDOMIZED, // placed as Block
               ITEM=EXACT, // item entity
               FALLING_BLOCK=EXACT, // falling block entity
               INVENTORY=EXACT, // block inventory
               ENTITY=EXACT, // entity inventory
               PLAYER=HIDDEN, // player inventory
            }"""
    )
    private Map<PositionType, VisibilityType> visibility = defaultVisibility;

    public static Config loadOrCreate() {
        if (!PATH.toFile().isFile()) {
            CommentedConfigurationNode node = LOADER.createNode();
            try {
                node.set(new Config());
                LOADER.save(node);
            } catch (Exception e) {
                LOGGER.warn("Failed to save default config to disk", e);
            }
        } else {
            try {
                return LOADER.load().get(Config.class);
            } catch (Exception e) {
                if (CONFIG != null) {
                    LOGGER.warn("Failed to load config, using previous value instead", e);
                    return CONFIG;
                }
                LOGGER.warn("Failed to load config, using default config instead", e);
            }
        }
        return new Config();
    }

    public VisibilityType getVisibility(PositionType type) {
        return visibility.getOrDefault(type, defaultVisibility.get(type));
    }

    public enum VisibilityType {
        RANDOMIZED,
        EXACT,
        HIDDEN
    }

    @ConfigSerializable
    public static class Messages {
        private static final NodeParser PARSER = NodeParser
            .builder()
            .globalPlaceholders()
            .quickText()
            .staticPreParsing()
            .build();

        @Comment("The message that is displayed when using '/dragon_egg bearer' and the visibility of the current " +
                 "position type is 'EXACT' (see visibility)")
        public MessageString bearerExact = new MessageString(
            PARSER,
            "<yellow>The %deg:item% is currently held by <gold>%deg:bearer%</> and was last seen at " +
            "<gold>%deg:pos%</>."
        );
        @Comment("The message that is displayed when using '/dragon_egg bearer' and the visibility of the current " +
                 "position type is 'RANDOMIZED' (see visibility)")
        public MessageString bearerRandomized = new MessageString(
            PARSER,
            "<yellow>The %deg:item% is currently held by <gold>%deg:bearer%</> and was last seen around " +
            "<gold>%deg:pos%</>."
        );
        @Comment("The message that is displayed when using '/dragon_egg bearer' and the visibility of the current " +
                 "position type is 'HIDDEN' (see visibility)")
        public MessageString bearerHidden = new MessageString(
            PARSER,
            "<yellow>The %deg:item% is currently held by <gold>%deg:bearer%</>."
        );
        @Comment("The message that is displayed when using '/dragon_egg bearer' but there is no bearer")
        public MessageString noBearer = new MessageString(PARSER, "<yellow>No one has snatched the %deg:item% yet.");
        @Comment("The message that is displayed when using '/dragon_egg bearer' but there is an error")
        public MessageString bearerError = new MessageString(PARSER, "<red>Currently not available.");
        @Comment("The dragon egg bearer has changed")
        public MessageString bearerChanged = new MessageString(
            PARSER,
            "<yellow><gold>%deg:bearer%</> now has the %deg:item%!"
        );
        @Comment("The message that is displayed when using '/dragon_egg info'")
        public MessageString info = new MessageString(
            PARSER,
            """
                
                
                
                <aqua><bold>The Dragon Egg Server Game</*>
                <gray>----------------------------</*>
                <yellow>Whoever has the %deg:item%, must place the %deg:item% <gold><hover show_text "\
                    When arriving at the base, you should quickly know where to look \
                    and the time needed for the search should be appropriate.\
                ">obvious</></> and <gold><hover show_text "\
                    You shouldn't have to destroy anything to get to the %deg:item%.\
                ">accessible for everyone</></> in the own base. \
                You can <gold><hover show_text "\
                    It's supposed to be fun for everybody, so please look out for another and fight fair. \
                    (It's best if you don't fight at all!)
                    The defense should not go beyond your own base and lost items (e.g. because of death) must be returned.\
                ">protect</></> it with traps and your own life, or put it in a huge vault, \
                but it has to be <gold><hover show_text "\
                    When arriving at the base, you should quickly know where to look \
                    and the time needed for the search should be appropriate.\
                ">obvious</></> where the %deg:item% is. \
                Everyone else now can steal the %deg:item% and has to place it in their base respectively.</*>
                <red><italic>You may only steal the egg, if the current egg bearer is online \
                or if they have been offline for at least 3 days!\
                """.replace("  ", "")
        );
    }
}

