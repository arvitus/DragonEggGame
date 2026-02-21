package de.arvitus.dragonegggame.config;

import de.arvitus.dragonegggame.api.DragonEggAPI.PositionType;
import eu.pb4.placeholders.api.parsers.NodeParser;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
            build
                .register(MessageString.class, new MessageString.Serializer(Messages.PARSER))
                .register(Action.class, Action.Serializer.INSTANCE)
                .register(CommandTemplate.class, CommandTemplate.Serializer.INSTANCE)
                .register(Condition.class, Condition.Serializer.INSTANCE)
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
    @Comment("Whether to prevent the Dragon Egg from entering an Ender Chest. " +
             "\nThis will also check the contents of container items like Shulker Boxes and Bundles.")
    public boolean blockEnderChest = true;
    @Comment("Whether to prevent the Dragon Egg from entering any container item (portable container)," +
             " e.g. Shulker Boxes and Bundles." +
             "\nThis is ignored in the creative inventory and does not check container items.")
    public boolean blockContainerItems = false;
    @Comment("Messages used throughout the mod")
    public Messages messages = new Messages();
    @Comment("The distance in blocks around the Dragon Egg where players count as 'nearby'")
    public int nearbyRange = 64;
    @Comment("""
        Actions that are executed on certain triggers.
        You can specify different actions and also specify a condition that must be met for the action to run.
        
        Available triggers:
            - 'deg:block' - When the egg is placed as a block.
            - 'deg:item' - When the egg becomes an item (e.g. is dropped).
            - 'deg:inventory' - When the egg is placed in a block inventory (e.g. chest).
            - 'deg:entity' - When the egg is placed in an entity inventory (e.g. item frame, chest boat)
            - 'deg:player' - When the egg enters a players inventory.
            - 'deg:falling_block' - When the egg becomes a falling block entity.
            - 'deg:second' - Every second.
        NOTE: All entities (items, falling blocks, players, etc.) will trigger multiple times if they move while holding the egg.
        
        The following placeholders are available in commands and use the format {{<placeholder>}}:
            - 'bearer' - The name of the bearer.
            - 'bearer_id' - The uuid of the bearer.
            - 'nearby' - Shortcut for '@a[predicate=deg:is_nearby]' (see below).
        
        The Mod also provides two predicates that can be used in commands (also in-game):
            - 'deg:is_bearer' - Matches the current bearer of the egg.
            - 'deg:is_nearby' - Matches players within the reward range of the egg.
        
        Additionally, you can use ${<expression>} to calculate a mathematical expression.
        For information on available functions and operators, see: https://www.objecthunter.net/exp4j/
        IMPORTANT: Make sure the result is a format that is compatible with the format expected by the command.
                   Most commands expect integer values, so make sure the calculation results in an integer.
                   The functions 'round(a, b)', 'floor(a)', and 'ceil(a)' can help with that.
        Available variables:
            - (int) 'bearerTime' - The time in seconds since the last bearer change.
            - (int) 'blockTime' - The time in seconds since the egg was last placed.
            - (int) 'totalBlockTime' - The total time in seconds the egg has been placed, since the last bearer change.
            - (int) 'playerTime' - The time in seconds since the egg has last entered the bearers inventory.
            - (int) 'totalPlayerTime' - The total time in seconds the egg has been in the bearers inventory, since the last bearer change.
            - (int) 'itemTime' - The time in seconds since the egg became an item entity.
            - (int) 'totalItemTime' - The total time in seconds the egg has been an item entity, since the last bearer change.
            - (int) 'entityTime' - The time in seconds since the egg was last placed in an entity inventory (not player).
            - (int) 'totalEntityTime' - The total time in seconds the egg has been in an entity inventory (not player), since the last bearer change.
            - (int) 'invTime' - The time in seconds since the egg was last placed in a block inventory.
            - (int) 'totalInvTime' - The total time in seconds the egg has been in a block inventory, since the last bearer change.
            - (int) 'fallingTime' - The time in seconds since the egg became a falling block entity.
            - (int) 'totalFallingTime' - The total time in seconds the egg has been a falling block entity, since the last bearer change.
            - (double) 'x' - The X coordinate of the egg, at the center of the block.
            - (double) 'y' - The Y coordinate of the egg, at the center of the block.
            - (double) 'z' - The Z coordinate of the egg, at the center of the block.
            - (int) 'randX' - The randomized X block coordinate of the egg.
            - (int) 'randY' - The randomized Y block coordinate of the egg.
            - (int) 'randZ' - The randomized Z block coordinate of the egg.
        Additional functions:
            - rnd(a) - returns a pseudo random number between 0 and a (0 <= rnd(a) < a)
            - min(a, b) - returns the smaller of a and b
            - max(a, b) - returns the larger of a and b
            - round(a, b) - rounds a to b decimal places
        Additional operators:
            - '==' - equals (returns 1 if true and 0 otherwise)
            - '!=' - not equals (returns 1 if true and 0 otherwise)
            - '<' - less than (returns 1 if true and 0 otherwise)
            - '>' - greater than (returns 1 if true and 0 otherwise)
            - '<=' - less than or equal (returns 1 if true and 0 otherwise)
            - '>=' - greater than or equal (returns 1 if true and 0 otherwise)
            - '&&' - logical and (returns 0 if at least one value is 0 and 1 otherwise)
            - '||' - logical or (returns 0 if both values are 0 and 1 otherwise)
        
        Format:
        [ // a list of Actions
          <Action1>, // see below for Action object format
          <Action2>,
          ...
        ]
        
        Action Object:
        {
          "trigger": "<trigger>", // the trigger that activates this Action (required at top level, otherwise ignored)
          "condition": "<expression>", // the condition that must be met (<expression> != 0) for the Action to run (optional)
          "actions": [ // a list of Actions to execute (optional)
            "<command>", // shorthand for an Action with only a command,
            <Action2>, // an Action object
            ...
          ],
          "command": "<command>" // a minecraft command to execute (optional)
          // you can use any valid Minecraft command here, including commands added by other mods
        }
        
        Example:
        [
          {
            "trigger": "deg:block",
            "condition": "blockTime == 0", // <-- only run if it was not already a block
            "actions": [
              "tellraw @a {\"text\":\"The Dragon Egg has been deployed. Go find it!\", \"color\":\"yellow\"}",
              {
                "condition": "totalBlockTime == 0", // <-- only run once when the egg is placed the first time
                "command": "effect give {{bearer_id}} minecraft:strength 300 1"
              } //                            ^-- will insert the uuid of the bearer
            ]
          },
          {
            "trigger": "deg:second",
            "condition": "blockTime == 0 && bearerTime % 30 == 0", // <-- runs every 30 seconds if the egg is not placed
            "actions": [
              {
                "condition": "bearerTime == 0", // <-- When the bearer changed (someone stole the egg)
                "command": "tellraw {{bearer}} {\"text\":\"The Dragon Egg needs to be placed down in the next 10 Minutes!\", \"color\":\"red\"}"
              }, //                     ^-- will insert the name of the bearer
              {
                "condition": "bearerTime == 300", // <-- 5 minutes after the bearer changed
                "command": "tellraw {{bearer}} {\"text\":\"The Dragon Egg needs to be placed down in the next 5 Minutes!\", \"color\":\"red\"}"
              }, //                     ^-- will insert the name of the bearer
              {
                "condition": "bearerTime >= 600", // <-- after 10 minutes and every 30 seconds thereafter
                "actions": [
                  "tellraw {{bearer}} {\"text\":\"The Dragon Egg needs to be placed down!\", \"color\":\"red\"}",
                  //           ^-- will insert the name of the bearer
                  "effect give {{bearer_id}} minecraft:poison 5 ${floor(bearerTime / 400)}",
                  //                 ^-- will insert the uuid of the bearer
                ]
              }
            ]
          }
        ]"""
    )
    public List<Action> actions = List.of();
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

    public static Config loadAndUpdateOrCreate() {
        Config config = new Config();
        if (!PATH.toFile().isFile()) {
            config.save();
            return config;
        }

        try {
            CommentedConfigurationNode node = LOADER.load();

            boolean update = false;
            if (!node.hasChild("actions")) {
                // visibility type NONE was removed
                node.node("visibility").removeChild("NONE");
                update = true;
            }
            if (!node.hasChild("block-ender-chest")) {
                // blockEnderChest and blockContainerItems were added
                update = true;
            }

            config = node.get(Config.class);

            if (update) {
                LOGGER.info("Detected old config file format, updating...");
                try {
                    Objects.requireNonNull(config).save();
                    LOGGER.info("Config file was updated to current format");
                } catch (Exception e) {
                    LOGGER.warn("Failed to update config file to new format", e);
                }
            }
        } catch (Exception e) {
            if (CONFIG != null) {
                LOGGER.warn("Failed to load config, using previous value instead", e);
                config = CONFIG;
            }
            LOGGER.warn("Failed to load config, using default config instead", e);
        }

        return config;
    }

    public boolean save() {
        CommentedConfigurationNode node = LOADER.createNode();
        try {
            node.set(this);
            LOADER.save(node);
        } catch (Exception e) {
            LOGGER.warn("Failed to save config to disk", e);
            return false;
        }
        return true;
    }

    public VisibilityType getVisibility(@Nullable PositionType type) {
        return visibility.getOrDefault(type, defaultVisibility.getOrDefault(type, VisibilityType.HIDDEN));
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

