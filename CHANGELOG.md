# Changelog 1.5
- Added Solider Zombie
- Added Dye Blaster
- Added Gravestone Block
	- Zombies will emerge from the ground near the gravestone during the night.
- Added The Graveyard biome
### Technical Changes

# Changelog 1.4.1
- Solar battery can be placed and will absorb nearby sun.
  - The light level and redstone level changes depending on the store amount of sun.
- Adjusted spawn rates and drop chances.
- Reduced sun fuel amount by half.
- Made sun a little easier to pick up
- Increased the amount of healing sun gives to plants.
- Disco zombie can now only spawn backup when there are < 3 backup dances nearby rather than < 4
- Updated list of projectiles that wallnut can deflect.
- Wall Nut can now be planted on most solid blocks
- Potatomine can be planted on sand and gravel blocks.
- Explosive plants will no longer continue to explode after being tamed.
- Endermen will now avoid plant projectiles
- Fixed a bug where plants could still damage player owners with "coopPlanting" and "playerCreditForPlantKills" enabled. 
- Mailboxes have been updated.
  - All players can now access mailboxes from any loaded chunk within the same dimension.
  - The address list is now sorted by distance from the current mailbox.
  - Added response messages when sending mail.
- Fixed issues with projectiles phasing through entities, hitting entities multiple times, and not being deflected
- The Watering Can is able to take water out of a cauldron by crouching.
- Improved Digger zombie pathfinding, block breaking speed, and what blocks it can break
- Gargantuar can only drop a plant pot helmet when killed by a plant
- Added 1.25% a chance for non-leader zombies to spawn with a flag when the local difficulty is < 1.2
- Separated server and client config options
- Fixed an issue where the server and client config settings would clash and cause errors.

# Changelog 1.4
- Added Doom-Shroom
  - Found in Basalt Deltas and in Bastion treasure chests.
- Added Sea-Shroom
    - Found in oceans and rivers and from fishing.
- Added Ducky Tube
	- Makes the user float in water while equipped.
    - Browncoats and Newspaper Zombies can spawn in lakes and oceans with the Ducky Tube. 
    - Zombies with Ducky Tubes spawn more often while it's raining.
- Added Watering Can
  - Can be obtained from a level 3 farmer or from fishing.
- Added Solar Battery
    - ***Currently not obtainable outside of Creative mode.***
    - Can store sun items and automatically picks up sun from plants.
    - Can be used for sun-based plant interactions.
- Plants now required water to grow.
	- Plants can be watered with Rain, Water Bottles, Water Buckets, or Watering Cans.
	- Water Buckets and the Watering Can will reduce the amount of sun needed when the Plant wakes up.
- Adjusted projectile firing logic for Plants and Zombies.
- Wild plants will only be aggro'd towards zombies by default, not other enemies.
- Wild Scaredy Shrooms now hide from the player.
- Plant explosions and other attacks from tamed plants will not damage the owner. If `coopPlanting` is eabled this extends to other players as well.
- Adjusted spawn rates.
### Technical Changes
- Leader Zombies can no longer spawn leaders as reinforcements.
- Added configuration options.
    - `coopPlanting` – When enabled, other players can dig up and interact with other players' plants.
    - `playerCreditForPlantKills` – When enabled, mobs killed by tamed plants will drop exp and player only drops.
    - `sunCostTamingThreshold` – Sun cost at which plants become significantly harder to tame. Plants with sun cost >= this value will have the lowest taming probability.
    - `coffeeBuffDuration` – How many ticks the coffee buff lasts.
    - `sunCost` – A list of entity ids paired with an integer amount.
	- `seedGrowTime` – The base time for growing seeds.
    - `extraGrowTimePerSun` – Extra time per sunCost that is added to the base time.
    - `zenPotTimeReduction` – This value is multiplied by the final growth time when plants are zen potted. _(1.0 = 100% reduction in sleep needed, aka no sleep.)_
    - `hydrationSunReduction` – When the plant has received water from a watering can or water bucket, this value will reduce the amount of sun needed to receive seeds when a plant wakes up. _(1.0 = 100% reduction in sun needed (minimum of 1))_
    - `plantPotDamageReduction` – Amount of enemy damage that is reduced when planted in any plant pot. _(1.0 = negate 100% of enemy damage.)_
    - `plantCooldownEnabled` – Toggle for cooldown when plaing plants.
    - `solarBatteryMax` – Maximum amount of sun that can be stored in the battery.
- Removed SeedPacket Component.
- Added StoredSun item component. Used by Solar Battery.
- Added StoredWater item component. Used by Watering Can.
- Added SunCost item component.

# Changelog 1.3
- Bug fixes.
- Villagers are scared of the Special Zombies.
- The Zombie Raid now ends on the last wave when the timer runs out.

# Changelog 1.2
- Bug fixes.
- Increased Time and Sun requirements for growing seeds.
- Adjusted some Zombies health and behavior.
	- Increased health overall.
	- Tweaked targeting priority.
	- All Star (while charging) and Gargantuar can now trample entities.
- Melonpult now spawns in jungle biomes.
- Gargantuar no longer drops Melonpult seeds.
- Added the Plant Pot Helmet.
	- Plants can be equipped on the players head using the helmet.
	- The helmet can drop from Gargantuar.
- Added the Coffee Bean.

# Changelog 1.1
- Bug fixes
	- Fixed sleeping animations
	- Fixed mailbox menu not refreshing
- Adjusted mob spawning and drop rates
- Adjust time and sun requirements for getting seeds
- Updated Chomper bite attack
- Added a chance to inflict a Toxic effect from Imp attacks


