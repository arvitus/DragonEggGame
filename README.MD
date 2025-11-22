<div align="center">

# Dragon Egg Game

[![Static Badge](https://img.shields.io/badge/modrinth-00AF5C?logo=modrinth&logoColor=white)](https://modrinth.com/mod/dragonegggame)
[![Static Badge](https://img.shields.io/badge/GitHub-181717?logo=github)](https://github.com/arvitus/DragonEggGame)
[![Static Badge](https://img.shields.io/badge/Discord-5662f6?logo=discord&logoColor=white)](https://discord.gg/xzdbetZVrn)

A Minecraft mod that adds a small, capture the flag-like minigame to your SMP, making use of the otherwise rarely used
Dragon Egg.

</div>

---

<h6 id="trivia"></h6>

# Trivia

On our SMP Server, we never had any use for the Dragon Egg. After we defeat the Ender Dragon, someone would usually just
take the Dragon Egg, put it in their Ender Chest and leave it there until the end of time. This way the Dragon Egg,
which there exists only one of, never had the opportunity to be used for something bigger. So, after seeing something
similar on another SMP, we decided to make a minigame out of it. This way the Dragon Egg would always be in play and
also encourage some player interaction. This mod helped us enforce the rules of the game and made it easier to play.
**Now we want to share it with you!**

<h6 id="rules"></h6>

# Rules

These are the rules we came up with for our server. Of course, you can change them however you like!

- Whoever is in possession of the dragon egg, has to place it somewhere accessible in their base.
- Everyone else can now try to steal the dragon egg and then has to place it in their own base, and so on.
- Stealing the egg is only allowed if the current bearer is online or has been offline for at least 3 days.
- It is allowed to hide the egg or protect it from getting stolen but one should be able to obtain it without breaking
  blocks or any massive technical knowledge.
- *It's supposed to be fun, so don't take it too seriously!*

<h6 id="features"></h6>

# Features

- Automatic and recursive tracking of the Dragon Egg (even in shulker boxes inside chests)
- Dragon Egg cannot be destroyed
- Dragon Egg may teleport to spawn, when trying to destroy it or if it is otherwise lost or too far from a player
- [Commands](#commands) for information about the game, the current bearer and the egg's (approximate) location
- Global message when the egg is stolen
- Extensive [customization options](#default-configs)
    - Per state (e.g. held by player, in chest, etc.) customization of egg visibility
    - All messages can be customized and
      support [placeholders and the QuickText format](https://placeholders.pb4.eu/user/general/)
    - Run [Actions](#actions) based on events (e.g. when the egg placed)
    - Customizable [BlueMap](https://modrinth.com/mod/bluemap) marker
- [BlueMap](https://modrinth.com/mod/bluemap) integration. *Discord integration is also planned in the far future*
- Custom [Placeholders](#placeholders)
- A rudimentary API (ask on [Discord](https://discord.gg/xzdbetZVrn) or look
  on [GitHub](https://github.com/arvitus/DragonEggGame) for usage)
- **IMPORTANT: It breaks when there is more than one Dragon Egg!**

<h6 id="commands"></h6>

# Commands

| Command              | Description                                          | Permission or OP Level      |
|----------------------|------------------------------------------------------|-----------------------------|
| `/dragon_egg bearer` | Info about the current bearer and the egg's location | `deg.bearer`, `0`, optional |
| `/dragon_egg info`   | Info about the game                                  | `deg.info`, `0`, optional   |
| `/deg`               | Mod info                                             | `deg.admin`, `4`            |
| `/deg reload`        | Reloads the config                                   | `deg.admin.reload`, `4`     |

<h6 id="placeholders"></h6>

# Placeholders

Placeholder format: `%placeholder%`

| Placeholder          | Description                                                     | Permission or OP Level                 |
|----------------------|-----------------------------------------------------------------|----------------------------------------|
| `deg:bearer`         | The current bearer of the egg                                   |                                        |
| `deg:pos`            | The current location of the egg, respecting visibility settings |                                        |
| `deg:exact_pos`      | The exact current location of the egg                           | `deg.placeholders.exact_pos`, `4`      |
| `deg:randomized_pos` | The randomized current location of the egg                      | `deg.placeholders.randomized_pos`, `4` |
| `deg:item`           | A hover text displaying the Dragon Egg                          |                                        |

<h6 id="predicates"></h6>

# Predicates

The mod provides predicates that can be used in commands (also in-game).

| Predicate       | Description                                         |
|-----------------|-----------------------------------------------------|
| `deg:is_bearer` | Matches the current bearer of the egg.              |
| `deg:is_nearby` | Matches players within the reward range of the egg. |

Example: `/execute as @a if predicate deg:is_bearer run say I am the bearer!`

<h6 id="actions"></h6>

# Actions

Since version `2.0` you can specify `Actions` that are triggered on certain [events](#triggers) and executed if the
condition is met.

Originally designed as a way to add rewards, `Actions` evolved into a flexible and powerful event-based system that lets
you add basically any functionality you want, however you want it. `Actions` allow you to do basically anything from
sending announcements to giving rewards (e.g. effects) for having the egg. They will even let you give better rewards
based on the time a player holds the egg.

Currently, an `Action` is a Minecraft command that is executed by the server, if a condition is met. You can use any
valid
Minecraft command, including commands added by other mods.

<h6 id="structure"></h6>

## Structure

```hocon
// An Action object
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
```

<h6 id="triggers"></h6>

## Triggers

Triggers define when an `Action` is executed.

| Trigger             | Description                                                                 |
|---------------------|-----------------------------------------------------------------------------|
| `deg:block`         | When the egg is placed as a block.                                          |
| `deg:item`          | When the egg becomes an item (e.g. is dropped).                             |
| `deg:inventory`     | When the egg is placed in a block inventory (e.g. chest).                   |
| `deg:entity`        | When the egg is placed in an entity inventory (e.g. item frame, chest boat) |
| `deg:player`        | When the egg enters a players inventory.                                    |
| `deg:falling_block` | When the egg becomes a falling block entity.                                |
| `deg:second`        | Every second.                                                               |

**NOTE:** *All entities (items, falling blocks, players, etc.) will trigger multiple times if they move while holding
the egg.*

<h6 id="command-placeholders"></h6>

## Command Placeholders

Command placeholders are available in commands and use the format `{{<placeholder>}}`, where `<placeholder>` must be
replaced with the placeholder name.

| Placeholder Name | Description                                                                 |
|------------------|-----------------------------------------------------------------------------|
| `bearer`         | The name of the bearer.                                                     |
| `bearer_id`      | The uuid of the bearer.                                                     |
| `nearby`         | Shortcut for `@a[predicate=deg:is_nearby]` (see [predicates](#predicates)). |

**NOTE:** *You cannot use regular [placeholders](#placeholders) here and vice versa!*

<h6 id="expressions"></h6>

## Expressions

Expressions are a way to calculate a value based on various [variables](#available-variables) related to the egg's
state. Expression variables are numbers and expressions return also a number. If used as a [condition](#structure), the
expression is considered true if the result is not equal to `0`.

Additionally, you can use `${<expression>}` in `Action` commands, where `<expression>` must be replaced with a valid
expression. The result of the expression will then be inserted in place of the placeholder.

**IMPORTANT:** *Make sure the result is a type that is compatible with the type expected by the command!
For example, most commands expect integer values, so make sure the calculation results in an integer in those cases.
The [functions](#additional-functions) `round(a, b)`, `floor(a)`, and `ceil(a)` can help with that.*

For information on default functions and operators, see: https://www.objecthunter.net/exp4j/

<h6 id="available-variables"></h6>

### Available Variables

| Variable Name      | Type     | Description                                                                                                   |
|--------------------|----------|---------------------------------------------------------------------------------------------------------------|
| `bearerTime`       | `int`    | The time in seconds since the last bearer change.                                                             |
| `blockTime`        | `int`    | The time in seconds since the egg was last placed.                                                            |
| `totalBlockTime`   | `int`    | The total time in seconds the egg has been placed, since the last bearer change.                              |
| `playerTime`       | `int`    | The time in seconds since the egg has last entered the bearers inventory.                                     |
| `totalPlayerTime`  | `int`    | The total time in seconds the egg has been in the bearers inventory, since the last bearer change.            |
| `itemTime`         | `int`    | The time in seconds since the egg became an item entity.                                                      |
| `totalItemTime`    | `int`    | The total time in seconds the egg has been an item entity, since the last bearer change.                      |
| `entityTime`       | `int`    | The time in seconds since the egg was last placed in an entity inventory (not player).                        |
| `totalEntityTime`  | `int`    | The total time in seconds the egg has been in an entity inventory (not player), since the last bearer change. |
| `invTime`          | `int`    | The time in seconds since the egg was last placed in a block inventory.                                       |
| `totalInvTime`     | `int`    | The total time in seconds the egg has been in a block inventory, since the last bearer change.                |
| `fallingTime`      | `int`    | The time in seconds since the egg became a falling block entity.                                              |
| `totalFallingTime` | `int`    | The total time in seconds the egg has been a falling block entity, since the last bearer change.              |
| `x`                | `double` | The X coordinate of the egg, at the center of the block.                                                      |
| `y`                | `double` | The Y coordinate of the egg, at the center of the block.                                                      |
| `z`                | `double` | The Z coordinate of the egg, at the center of the block.                                                      |
| `randX`            | `int`    | The randomized X block coordinate of the egg.                                                                 |
| `randY`            | `int`    | The randomized Y block coordinate of the egg.                                                                 |
| `randZ`            | `int`    | The randomized Z block coordinate of the egg.                                                                 |

<h6 id="additional-functions"></h6>

### Additional Functions

For a list of default functions, see [here](https://www.objecthunter.net/exp4j/#Built-in_functions).

| Function Signature | Description                                                            |
|--------------------|------------------------------------------------------------------------|
| `rnd(a)`           | Returns a pseudo random number between `0` and `a` (`0 <= rnd(a) < a`) |
| `min(a, b)`        | Returns the smaller of `a` and `b`                                     |
| `max(a, b)`        | Returns the larger of `a` and `b`                                      |
| `round(a, b)`      | Rounds `a` to `b` decimal places                                       |

<h6 id="additional-operators"></h6>

### Additional operators

For a list of default operators, see [here](https://www.objecthunter.net/exp4j/).

| Operator | Name                  | Returns                                            |
|----------|-----------------------|----------------------------------------------------|
| `==`     | Equals                | `1` if true and `0` otherwise                      |                       
| `!=`     | Not equals            | `1` if true and `0` otherwise                      |                   
| `<`      | Less than             | `1` if true and `0` otherwise                      |                     
| `>`      | Greater than          | `1` if true and `0` otherwise                      |                  
| `<=`     | Less than or equal    | `1` if true and `0` otherwise                      |           
| `>=`     | Greater than or equal | `1` if true and `0` otherwise                      |        
| `&&`     | Logical and           | `0` if at least one value is `0` and `1` otherwise | 
| `\|\|`   | Logical or            | `0` if both values are `0` and `1` otherwise       |

<h6 id="example"></h6>

### Example

```hocon
actions = [
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
]
```

<h6 id="default-configs"></h6>

# Default Configs

The config files are too long to be included here. You can find the default configs for different mod versions here:

- [**Version `1.x`**](https://github.com/arvitus/DragonEggGame/blob/main/assets/configs/config_v1.conf)
- [**Version `2.x`**](https://github.com/arvitus/DragonEggGame/blob/main/assets/configs/config_v2.conf)

<h6 id="tips"></h6>

# Tips

- If the mod says that the egg is in a players inventory, but it is not, it is most likely in the player's ender chest.