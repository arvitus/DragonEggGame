<div align="center">

# Dragon Egg Game | SMP Minigame

[![Static Badge](https://img.shields.io/badge/modrinth-00AF5C?logo=modrinth&logoColor=white)](https://modrinth.com/mod/dragonegggame)
[![Static Badge](https://img.shields.io/badge/GitHub-181717?logo=github)](https://github.com/arvitus/DragonEggGame)
[![Static Badge](https://img.shields.io/badge/Discord-5662f6?logo=discord&logoColor=white)](https://discord.gg/xzdbetZVrn)

A Minecraft mod that adds a small, capture the flag-like minigame to your SMP, making use of the otherwise rarely used
Dragon Egg.

</div>

---

## Trivia

On our SMP Server, we never had any use for the Dragon Egg. After we defeat the Ender Dragon, someone would usually just
take the Dragon Egg, put it in their Ender Chest and leave it there until the end of time. This way the Dragon Egg,
which there exists only one of, never had the opportunity to be used for something bigger. So, after seeing something
similar on another SMP, we decided to make a minigame out of it. This way the Dragon Egg would always be in play and
also encourage some player interaction. This mod helped us enforce the rules of the game and made it easier to play.
**Now we want to share it with you!**

## Rules

These are the rules we came up with for our server. Of course, you can change them however you like!

- Whoever is in possession of the dragon egg, has to place it somewhere accessible in their base.
- Everyone else can now try to steal the dragon egg and then has to place it in their own base, and so on.
- Stealing the egg is only allowed if the current bearer is online or has been offline for at least 3 days.
- It is allowed to hide the egg or protect it from getting stolen but one should be able to obtain it without breaking
  blocks or any massive technical knowledge.
- *It's supposed to be fun, so don't take it too seriously!*

## Features

- Automatic and recursive tracking of the Dragon Egg (even in shulker boxes inside chests)
- Dragon Egg cannot be destroyed
- Dragon Egg may teleport to spawn, when trying to destroy it or if it is otherwise lost or too far from a player
- [Commands](#commands) for information about the game, the current bearer and the egg's (approximate) location
- Global message when the egg is stolen
- Extensive [customization options](#config-default)
    - Per state (e.g. held by player, in chest, etc.) customization of egg visibility
    - All messages can be customized and
      support [placeholders and the QuickText format](https://placeholders.pb4.eu/user/general/)
    - Customizable [BlueMap](https://modrinth.com/mod/bluemap) marker
- [BlueMap](https://modrinth.com/mod/bluemap) integration. *Discord integration is also planned in the far future*
- Custom [Placeholders](#placeholders)
- A rudimentary API (ask on [Discord](https://discord.gg/xzdbetZVrn) or look
  on [GitHub](https://github.com/arvitus/DragonEggGame) for usage)
- **IMPORTANT: It breaks when there is more than one Dragon Egg!**

<h3 id="commands"></h3>

## Commands

| Command              | Description                                          | Permission or OP Level      |
|----------------------|------------------------------------------------------|-----------------------------|
| `/dragon_egg bearer` | Info about the current bearer and the egg's location | `deg.bearer`, `0`, optional |
| `/dragon_egg info`   | Info about the game                                  | `deg.info`, `0`, optional   |
| `/deg`               | Mod info                                             | `deg.admin`, `4`            |
| `/deg reload`        | Reloads the config                                   | `deg.admin.reload`, `4`     |

<h3 id="placeholders"></h3>

## Placeholders

Placeholder format: `%placeholder%`

| Placeholder          | Description                                                     | Permission or OP Level                 |
|----------------------|-----------------------------------------------------------------|----------------------------------------|
| `deg:bearer`         | The current bearer of the egg                                   |                                        |
| `deg:pos`            | The current location of the egg, respecting visibility settings |                                        |
| `deg:exact_pos`      | The exact current location of the egg                           | `deg.placeholders.exact_pos`, `4`      |
| `deg:randomized_pos` | The randomized current location of the egg                      | `deg.placeholders.randomized_pos`, `4` |
| `deg:item`           | A hover text displaying the Dragon Egg                          |                                        |

<h3 id="config-default"></h3>

## Config (default)

```hocon
# The description of the BlueMap area marker
area-marker-description = "The dragon egg is somewhere in this area."
# The color of the BlueMap marker as a decimal value.
# Default: 2818132 (Purple)
marker-color = 2818132
# The name of the BlueMap marker
marker-name = "Dragon Egg"
# Messages used throughout the mod
messages {
  # The dragon egg bearer has changed
  bearer-changed = "<yellow><gold>%deg:bearer%</> now has the %deg:item%!"
  # The message that is displayed when using '/dragon_egg bearer' but there is an error
  bearer-error = "<red>Currently not available."
  # The message that is displayed when using '/dragon_egg bearer' and the visibility of the current position type is 'EXACT' (see visibility)
  bearer-exact = "<yellow>The %deg:item% is currently held by <gold>%deg:bearer%</> and was last seen at <gold>%deg:pos%</>."
  # The message that is displayed when using '/dragon_egg bearer' and the visibility of the current position type is 'HIDDEN' (see visibility)
  bearer-hidden = "<yellow>The %deg:item% is currently held by <gold>%deg:bearer%</>."
  # The message that is displayed when using '/dragon_egg bearer' and the visibility of the current position type is 'RANDOMIZED' (see visibility)
  bearer-randomized = "<yellow>The %deg:item% is currently held by <gold>%deg:bearer%</> and was last seen around <gold>%deg:pos%</>."
  # The message that is displayed when using '/dragon_egg info'
  info = "\n\n\n<aqua><bold>The Dragon Egg Server Game</*>\n<gray>----------------------------</*>\n<yellow>Whoever has the %deg:item%, must place the %deg:item% <gold><hover show_text \"When arriving at the base, you should quickly know where to look and the time needed for the search should be appropriate.\">obvious</></> and <gold><hover show_text \"You shouldn't have to destroy anything to get to the %deg:item%.\">accessible for everyone</></> in the own base. You can <gold><hover show_text \"It's supposed to be fun for everybody, so please look out for another and fight fair. (It's best if you don't fight at all!)\nThe defense should not go beyond your own base and lost items (e.g. because of death) must be returned.\">protect</></> it with traps and your own life, or put it in a huge vault, but it has to be <gold><hover show_text \"When arriving at the base, you should quickly know where to look and the time needed for the search should be appropriate.\">obvious</></> where the %deg:item% is. Everyone else now can steal the %deg:item% and has to place it in their base respectively.</*>\n<red><italic>You may only steal the egg, if the current egg bearer is online or if they have been offline for at least 3 days!"
  # The message that is displayed when using '/dragon_egg bearer' but there is no bearer
  no-bearer = "<yellow>No one has snatched the %deg:item% yet."
}
# The description of the BlueMap point marker
point-marker-description = "Come and get it!"
# The URL of the BlueMap point marker icon
point-marker-icon = "https://minecraft.wiki/images/thumb/Dragon_Egg_JE4.png/150px-Dragon_Egg_JE4.png"
# The radius that is used to randomize the dragon egg position.
# Default: 25
search-radius = 25
# The visibility of the dragon egg for each position type.
# Default: {
#   BLOCK=RANDOMIZED, // placed as Block
#   ITEM=EXACT, // item entity
#   FALLING_BLOCK=EXACT, // falling block entity
#   INVENTORY=EXACT, // block inventory
#   ENTITY=EXACT, // entity inventory
#   PLAYER=HIDDEN, // player inventory
# }
visibility {
  BLOCK = RANDOMIZED
  ENTITY = EXACT
  "FALLING_BLOCK" = EXACT
  INVENTORY = EXACT
  ITEM = EXACT
  NONE = HIDDEN
  PLAYER = HIDDEN
}
```

## Tips

- If the mod says that the egg is in a players inventory, but it is not, it is most likely in the player's ender chest.