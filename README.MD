<div align="center">

# Dragon Egg Game

[![Static Badge](https://img.shields.io/badge/modrinth-00AF5C?logo=modrinth&logoColor=white)](https://modrinth.com/mod/dragonegggame)
[![Static Badge](https://img.shields.io/badge/GitHub-181717?logo=github)](https://github.com/arvitus/DragonEggGame)
[![Static Badge](https://img.shields.io/badge/Wiki-181717?logo=github)](https://github.com/arvitus/DragonEggGame/wiki)
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
    - Prevent the egg from entering Ender Chests or Shulker Boxes and Bundles
- [BlueMap](https://modrinth.com/mod/bluemap) integration. *Discord integration is also planned in the far future*
- Custom [Placeholders](#placeholders)
- A rudimentary API (ask on [Discord](https://discord.gg/xzdbetZVrn) or look
  on [GitHub](https://github.com/arvitus/DragonEggGame) for usage)
- **IMPORTANT: It breaks when there is more than one Dragon Egg!**

<h6 id="commands"></h6>

# Commands

| Command              | Description                                          | Permission         | OP Level    | Default |
|----------------------|------------------------------------------------------|--------------------|-------------|:-------:|
| `/dragon_egg bearer` | Info about the current bearer and the egg's location | `deg.bearer`       | `ALL (0)`   |    ✅    |
| `/dragon_egg info`   | Info about the game                                  | `deg.info`         | `ALL (0)`   |    ✅    |
| `/dragon_egg help`   | List available commands                              | `deg.help`         | `ALL (0)`   |    ✅    |
| `/deg`               | Mod info                                             | `deg.admin.info`   | `OWNER (4)` |    ❌    |
| `/deg reload`        | Reloads the config                                   | `deg.admin.reload` | `OWNER (4)` |    ❌    |

<h6 id="placeholders"></h6>

# Placeholders

Placeholder format: `%placeholder%`

| Placeholder          | Description                                                     | Permission                        | OP Level    | Default |
|----------------------|-----------------------------------------------------------------|-----------------------------------|-------------|:-------:|
| `deg:bearer`         | The current bearer of the egg                                   |                                   |             |    ✅    |
| `deg:pos`            | The current location of the egg, respecting visibility settings |                                   |             |    ✅    |
| `deg:exact_pos`      | The exact current location of the egg                           | `deg.placeholders.exact_pos`      | `OWNER (4)` |    ❌    |
| `deg:randomized_pos` | The randomized current location of the egg                      | `deg.placeholders.randomized_pos` | `OWNER (4)` |    ❌    |
| `deg:item`           | A hover text displaying the Dragon Egg                          |                                   |             |    ✅    |

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

Since version `2.0` you can specify `Actions` that are triggered on
certain [events](https://github.com/arvitus/DragonEggGame/wiki/Actions#triggers) and executed if the condition is met.

Originally designed as a way to add rewards, `Actions` evolved into a flexible and powerful event-based system that lets
you add basically any functionality you want, however you want it. `Actions` allow you to do anything from
sending announcements to giving rewards (e.g. effects) for having the egg. They will even let you give better rewards
based on the time a player holds the egg.

Currently, an `Action` is a Minecraft command that is executed by the server, if a condition is met. You can use any
valid Minecraft command, including commands added by other mods.

**You can read more about how to use `Actions` and what you can do with them in
the [wiki](https://github.com/arvitus/DragonEggGame/wiki/Actions)!**

<h6 id="default-configs"></h6>

# Default Configs

The config files are too long to be included here. You can find the default configs for different mod versions here:

- [**Version `1.x`**](https://github.com/arvitus/DragonEggGame/blob/main/assets/configs/config_v1.conf)
- [**Version `2.x`**](https://github.com/arvitus/DragonEggGame/blob/main/assets/configs/config_v2.conf)

<h6 id="tips"></h6>

# Tips

- If the mod says that the egg is in a player's inventory, but it is not, it is most likely in the player's Ender Chest.