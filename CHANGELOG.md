# Changelog 1.5
### Additions
#### General:   
- Plantz Flag now reduces the cooldown of nearby plants by 20%. This effect does not stack.
- When placing plants in plant pots, their initial rotation will be orientated the same as the pot.
- While a plant is equipped using the Plant Pot Helmet, you can place them back in plant pots by shift clicking.
- Added Brainz Alloy.
  - Drops from the Grave Loot, Raid rewards, Rob Zombies, Super Brainz, Zombie Bots, and various other methods.
  - Used for crafting blocks and special zombie tech.
- Added Brainz Alloy block set.
  - Blocks include: Brainz Alloy Block, Stairs, Slab, Treaded Brainz Alloy Block, Reinforced Brainz Alloy Block, and Brainz Alloy Fences.
- Added Time Machine.
  - ***Currently not obtainable outside of Creative mode.***
- Added Dye Blaster
  - Has a 40% chance to consume dye as ammo.
  - Players and mobs shot with the blaster will be given the "painted" mob effect.
- Added Balloons
  - Balloons are attached to leads by default and can be tied to other mobs and fences.
  - Balloons pull the holder upward slightly. By carrying enough balloons, you can negate fall damage or even begin to float.
- Added Gravestone Block
  - Zombies will emerge from the ground near the gravestone during the night.
- Added The Graveyard biome.
- Added Garden Gnome Blocks.
- The Football Helmet now gives increased speed and step height while sprinting. Nearby Mobs will also be knocked back.
- Added Freeze Mob Effect.
- Added "Hero of the Garden" Effect.
  - Applies after defeating a raid.
  - While the effect is active, interacting with a mailbox will grant loot for each completed wave in the raid.
  - Special Waves will grant loot unique to that wave.
#### Plants:
- Added Electric Peashooter
  - Acquired by striking a peashooter with lightning.
- Added Lightning Reed
  - Found in swamps and near rivers during a thunderstorm.
- Added Tangle Kelp
  - Found in swamps and mangroves.
- Added Explode-O-Nut
  - Acquired from Wall Nut zen-gardening.
- Added Grave Buster
  - Found in the Graveyard and Pale Garden biomes.
  - Can be used on Gravestones to acquire Grave Loot.
- Snow Peas will now apply the Freeze effect rather than Slowness and Weakness.
- Added plant transformations
  - Some plants can be transformed into other plants under certain conditions.
- Added Seed Packet Mutations
  - When zen-gardening a plant, seeds for a different plant may be produced instead.
    - Wall Nut has a 5% chance to produce Explode-O-Nut seeds.
    - Explode-O-Nut has an 80% chance to produce Wall Nut seeds.
    - Electric Peashooter has a 95% chance to produce Peashooter seeds.
    - Peashooter has a 10% chance to produce Repeater seeds.
    - Repeater has a 60% chance to produce Peashooter seeds.
- Repeaters no longer spawn naturally.
#### Zombies:
- Updated the Zombie Raid event.
  - Added Special wave types:
    - "Bucket Brigade" can occur during waves 1–3.
    - "Half-time Showdown" can occur during waves 2–5.
    - "Winter Wonderland" can occur during waves 4–9.
    - "Pirate Invasion" can occur during waves 5–14.
    - "Robo Army" can occur during waves 6–15.
    - "League of Awesomeness" can occur during waves 8–19.
  - When a raid is completed, all players will receive the Hero of the Garden effect.
  - While the effect is active, interacting with a mailbox will grant special loot for each completed wave in the raid.
- Added Desert, Snow, and Pirate Variants for the Browncoat Zombie.
  - The pirate browncoat will spawn during the "Pirate Invasion" special wave.
  - The snow browncoat will spawn during the "Winter Wonderland" special wave.
- Added a Yeti and Pirate Variant for the Imp,
  - Yeti Imps apply the Freeze effect and spawn during the "Winter Wonderland" special wave.
  - Pirate Imps spawn in an explosive barrel and spawn during the "Pirate Invasion" special wave.
- Added a Pirate Variant for Gargantuar that spawns during the "Pirate Invasion" special wave.
- Added Engineer Zombie
  - Spawns during the "Robo Army" zombie wave and normal waves after the end credits have been seen.
  - Will avoid players and plants and will attempt to build zombie bots to attack.
- Added Soldier Zombie
  - Spawns during the "Robo Army" special wave.
  - Uses the dye blaster.
- Added Robo Zombie
  - Spawns during the "Robo Army" special wave.
- Added Pirate Captain Zombie
  - Spawns during the "Pirate Invasion" special wave. 
- Added Super Brainz
  - Spawns during the "League of Awesomeness" special zombie raid.
- Zombies will now attack Wall Nuts when walking into them.
### Technical Changes

# Changelog 1.4.2
### Hotfix
- Fixed a client-side crash on initialization.

# Changelog 1.4.1
### Additions:
- Added Bonk Choy
  - Spawns in Bamboo Jungle and Savanna biomes.
- Solar battery can be placed and will absorb nearby sun.
  - The light level and redstone level changes depending on the store amount of sun.
- Added Electrified Mob Effect.
  - Being struck by lightning will now apply the Electrified effect.
- Added translations for seven new languages.
  - Spanish - @Maxi1978
  - Portuguese - @theplayeris1, @Arthurow
  - Filipino - @Arseus
  - Turkish - @TangHere
  - Chinese - @_spos777
  - Japanese - @_spos777
  - Russian - @anyjerk
### Fixes/Changes:
#### General:
- Separated server and client config files
- Fixed an issue where the server and client config settings would clash and cause errors.
- Adjusted spawn rates and drop chances.
- The Watering Can is able to take water out of a cauldron by crouching.
- Mailboxes have been updated.
  - All players can now access mailboxes from any loaded chunk within the same dimension.
  - The address list is now sorted by distance from the current mailbox.
  - Added response messages when sending mail.
- Reduced sun fuel amount by half.
- Made sun a little easier to pick up
- Increased the amount of healing sun gives to plants.
- Fixed issues with projectiles phasing through entities, hitting entities multiple times, and not being deflected
- Easter egg skins no longer apply when any custom name is given.
#### Plants:
- Updated list of projectiles that wallnut can deflect.
- Wall Nut can now be planted on most solid blocks
- Potatomine can be planted on sand and gravel blocks.
- Explosive plants will no longer continue to explode after being tamed.
- Endermen will now avoid plant projectiles
- Fixed a bug where plants could still damage player owners when "coopPlanting" and "playerCreditForPlantKills" were enabled.
#### Zombies:
- Disco zombie can now only spawn backup when there are < 3 backup dances nearby rather than < 4
- Improved Digger zombie pathfinding, block breaking speed, and what blocks it can break
- Gargantuar can only drop a plant pot helmet when killed by a plant
- Added 1.25% a chance for non-leader zombies to spawn with a flag when the local difficulty is < 1.2

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


