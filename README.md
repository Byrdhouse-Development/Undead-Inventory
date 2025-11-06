# UndeadInventory

A Spigot plugin that causes humanoid mobs to collect the inventories of players they kill, and allows players to retrieve those inventories by killing the mob.

## Features
- When a player is killed by a humanoid mob (Steve-like), the mob is renamed to `Killer of {player}` and takes the player's equipped items (main hand, off hand, armor).
- The rest of the player's inventory is stored in the mob's persistent data container (simulating a gravestone inventory).
- If the mob kills additional players, their inventories are appended to the mob's stored inventory.
- When a player kills a `Killer of {player}` mob, all stored inventories are given to the killer.

## Supported Mobs
- Zombie, Husk, Drowned
- Skeleton, Bogged, Stray, Wither Skeleton
- Piglin, Piglin Brute, Zombie Piglin
- Villager, Zombie Villager
- Witch
- Evoker, Vindicator, Pillager

## Usage
1. Build the plugin with your preferred Java build tool.
2. Place the JAR in your server's `plugins` folder.
3. Restart the server.

## License
MIT
