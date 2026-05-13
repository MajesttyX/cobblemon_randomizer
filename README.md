# Cobblemon Randomizer

Cobblemon Randomizer is a NeoForge addon for Cobblemon that turns a normal Cobblemon playthrough into a randomized Nuzlocke-style challenge.

Players begin with randomized starter choices, then use Challenge Tokens to unlock legal target Pokémon from different biomes. The Challenge Journal tracks available targets, locked targets, completed targets, failed challenges, fallen Pokémon, and overall run status.

## Features

### Randomized Starters

Cobblemon Randomizer replaces the normal Cobblemon starter selection with a randomized starter category.

Starter options are generated per player and saved so the same player keeps their generated choices for that run.

Configurable starter settings include:

- Starter selection size
- Starter level
- Minimum starter IV value
- Maximum starter IV value
- Whether baby Pokémon can appear
- Whether single-stage Pokémon can appear
- Whether Legendary Pokémon can appear
- Whether Mythical Pokémon can appear
- Whether Ultra Beasts can appear
- Whether Paradox Pokémon can appear

### Challenge Tokens

Challenge Tokens are used to unlock legal target Pokémon.

Right-click a Challenge Token while standing in a biome to unlock that biome’s target Pokémon. The biome determines the target, but the target Pokémon may be caught anywhere as long as it is caught during battle.

The Challenge Token is consumed when a new target is unlocked.

### Challenge Journal

After choosing a starter, the player receives a Challenge Journal.

The journal tracks:

- Available Targets
- Locked Until Tomorrow
- Failed Challenges
- Completed Challenges
- Fallen Pokémon
- Run Status
- Rules and token instructions

The journal refreshes when opened so retry-ready targets are shown correctly.

### Catch Rules

Only currently available target Pokémon may be caught.

Target Pokémon must be caught during battle. Direct overworld catching is blocked.

If the player tries to catch the wrong Pokémon, or tries to catch a target that is locked or failed, the capture is blocked and the player receives a message explaining why.

### Failed Target Rules

A target can fail if:

- the target Pokémon faints
- the player flees from the battle

Depending on config, failed targets either:

- become available again the next Minecraft day, or
- fail permanently

### Nuzlocke Death Rules

When a player-owned Pokémon faints in battle, it becomes Nuzlocke-dead.

Dead Pokémon:

- remain at 0 HP
- cannot be healed back into usability
- cannot be traded if trade blocking is enabled
- appear in the Fallen Pokémon section of the Challenge Journal

If the player has no usable Pokémon remaining, the run fails.

### Hardcore Support

In Hardcore worlds, failing the Nuzlocke triggers Minecraft’s normal Hardcore death flow.

## Commands

Cobblemon Randomizer uses the main command `/cobblemonrandomizer`.

### Player Commands

These commands are available to normal players.

| Command | Description |
|---|---|
| `/cobblemonrandomizer challenges` | Shows your recorded biome target unlocks and each target’s current status. |
| `/crandomizer challenges` | Shortcut version of `/cobblemonrandomizer challenges`. |
| `/cobblemonrandomizer run_status` | Shows your current Nuzlocke run state, usable Pokémon count, fallen Pokémon count, and target summary. |
| `/crandomizer run_status` | Shortcut version of `/cobblemonrandomizer run_status`. |
| `/cobblemonrandomizer refresh_retries` | Checks whether any of your failed targets are ready to become available again. |
| `/crandomizer refresh_retries` | Shortcut version of `/cobblemonrandomizer refresh_retries`. |

### Admin Commands

These commands require operator/admin permission.

| Command | Description |
|---|---|
| `/cobblemonrandomizer challenges <player>` | Shows another player’s recorded biome target unlocks and each target’s current status. |
| `/crandomizer challenges <player>` | Shortcut version of `/cobblemonrandomizer challenges <player>`. |
| `/cobblemonrandomizer run_status <player>` | Shows another player’s Nuzlocke run state, usable Pokémon count, fallen Pokémon count, and target summary. |
| `/crandomizer run_status <player>` | Shortcut version of `/cobblemonrandomizer run_status <player>`. |
| `/cobblemonrandomizer refresh_retries <player>` | Checks whether any failed targets are ready to become available again for the selected player. |
| `/crandomizer refresh_retries <player>` | Shortcut version of `/cobblemonrandomizer refresh_retries <player>`. |
| `/cobblemonrandomizer clear_challenges` | Clears your recorded biome target unlocks. |
| `/crandomizer clear_challenges` | Shortcut version of `/cobblemonrandomizer clear_challenges`. |
| `/cobblemonrandomizer clear_challenges <player>` | Clears the selected player’s recorded biome target unlocks. |
| `/crandomizer clear_challenges <player>` | Shortcut version of `/cobblemonrandomizer clear_challenges <player>`. |
| `/cobblemonrandomizer clear_deaths` | Clears your Nuzlocke death data and removes Pokémon-level death marks from your party and PC storage. |
| `/crandomizer clear_deaths` | Shortcut version of `/cobblemonrandomizer clear_deaths`. |
| `/cobblemonrandomizer clear_deaths <player>` | Clears the selected player’s Nuzlocke death data and removes Pokémon-level death marks from their party and PC storage. |
| `/crandomizer clear_deaths <player>` | Shortcut version of `/cobblemonrandomizer clear_deaths <player>`. |
| `/cobblemonrandomizer reset_run` | Resets your run by clearing biome target unlocks, Nuzlocke death data, and Pokémon-level death marks from party and PC storage. |
| `/crandomizer reset_run` | Shortcut version of `/cobblemonrandomizer reset_run`. |
| `/cobblemonrandomizer reset_run <player>` | Resets the selected player’s run by clearing biome target unlocks, Nuzlocke death data, and Pokémon-level death marks from party and PC storage. |
| `/crandomizer reset_run <player>` | Shortcut version of `/cobblemonrandomizer reset_run <player>`. |
