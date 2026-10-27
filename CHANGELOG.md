# Changelog 1.6
### Additions
#### General:
- Added Time Machine.
  - ***Currently not obtainable outside of Creative mode.***
- Added Citron

# Changelog 1.5.2
### Hotfix
- Pumpkins are no longer duplicated when using a Gardening Glove.

# Changelog 1.5.1
### Changes / Additions
- Added Pumpkin Plant.
  - Can be obtained as rewards for the Zomboween special wave or from carving pumpkins.
  - When applied to a plant, it will completely tank any given damage until destroyed.
- Updated Seed Mutation chances:
```
  - Peashooter: 50% Electric Peashooter seeds during Thunderstorms.
  - Peashooter: 50% Fire Peashooter seeds while in hot biomes.
  - Peashooter: 10% Repeater seeds anywhere.
  - Electric Peashooter: 15% Peashooter seeds during Clear/Rainy weather.
  - Fire Peashooter: 15% Peashooter seeds while NOT in hot biomes.
  - Repeater: No longer has a mutation (always drops repeater).
  - Melon Pult: 33.3% Winter Melon seeds in cold biomes.
  - Winter Melon: 15% Melon Pult while NOT in cold biomes.
  - Wallnut: 8% Explode-o-Nut seeds anywhere.
  - Explode-o-Nut: 20% Wallnut seeds anywhere.
```
- Added "Zomboween" special wave.
  - Has a chance to occur 2 weeks before and 1 day after October 31st.
  - Can happen within waves 4–11.
- Added a Boss Bar during boss waves.
  - If a Boss is not defeated by the end of a boss wave, the raid will be lost.
- Reworked the Digger Zombie's behavior.
  - When 16–40 blocks away from its target, The Digger Zombie will attempt to tunnel into the ground closer to its target.
- Added Vampire Browncoat variant.
  - Has a chance to appear during Halloween season.
- Balloon Zombies can drop balloons when killed.
- Unleashed Balloons will pop after 2 minutes.
- Hypnotized mobs will now target non-hypnotized mobs of the same type as well as other enemies.
- Reduced how often and how many graves the grave digger will dig.
- During a raid, the center flag will display a beacon beam while far away.
- Plants will now only snap onto blocks that they can survive on. 
- Plant and Brainz Flags can only be placed while holding shift.
- Plant and Zombie projectiles are now ignored by:
  - Armor Stands
  - Boats
  - Minecarts
  - Item Frames
- The Skillet item from [Farmer's Delight](https://modrinth.com/mod/farmers-delight-refabricated) can now be equipped on your head.
- Added nametag ester eggs for the Sunflower (Vampire) and Fire Peashooter (Blaze).
### Fixes / Technical Changes
- Reduced Grave Digger spawn chance during raids.
- Plants and Zombies no longer pop their own balloons. 
- Zombie Robots built by the Engineer will vanish at the end of the raid.
- Reduced the range for Robo Zombie.
- Raid zombies will no longer spawn on top of trees.
- Optimized zombie pathfinding during raids.
- Optimized some checks on idle plants.
- Puff Shrooms, Sun Shrooms, and Scaredy Shrooms now spawn again. :,)
- Fixed a bug that caused the game to hang when processing certain plants while saving.
- Added a new server config option: `graveStoneActivationRange`.
  - Sets the range at which Gravestone can spawn zombies. The default value is 24.0
- Added a new server config option: `pumpkinArmorAmount`.
  - Sets the amount of pumpkin armor gets applied. The default value is 35.
- Updated seed packet mutations to accept an item as a result:
  - The `result` field either accept and `entity_type` or and object:
    - `item` - an item id
    - `count` - a value range. Defaults to `{ "min": 1, "max": 1 }` (optional)
#### Example files:
```
{
  "mutations": [
    {
      "biome": "minecraft:forest",
      "chance": 1.0,
      "result": {
        "item": "minecraft:apple"
      }
    },
    {
      "chance": 0.5,
      "result": {
        "item": "minecraft:emerald",
        "count": {
          "min": 2,
          "max": 8
        }
      }
    }
  ]
}
```

# Changelog 1.5
### General:   
- Plantz Flag will now power-up nearby plants, reducing their cooldown by 20%. This effect does not stack.
- When placing plants in plant pots, their initial rotation will be orientated the same as the pot.
- While a plant is equipped using the Plant Pot Helmet, you can place them back in plant pots by shift clicking.
- Added Brainz Alloy
  - Drops from Grave Loot, Raid rewards, Robot Zombies, Super Brainz, Gargantuar, and various other methods.
  - Used for crafting blocks and special zombie tech.
- Added Brainz Alloy block set
  - Blocks include: Brainz Alloy Block, Stairs, Slab, Treaded Brainz Alloy Block, Reinforced Brainz Alloy Block, and Brainz Alloy Fences.
- Added Gardening Gloves
  - Can be obtained from a level 3 farmer or from raid rewards.
  - Lets players pet plants with right-click or pick-up and hold one plant by shift-clicking.
  - Can harvest crops with right click and can auto replant when the player has the right seeds/crops in their inventory.
  - Can also be used to roll Wall Nuts by punching them.
- Farmer Villagers trades have been added or changed.
  - Level 1: An emerald for 32 Sun.
  - Level 2: Watering Can and Potatomine seeds.
  - Level 3: Gardening Gloves.
- The Solar Battery can now be crafted with 4 x brainz alloy, 1 x reinforced brainz alloy block, 1 x daylight detector, and 2 x glass.
- Added Dye Blaster
  - Has a 40% chance to consume dye as ammo.
  - Players and mobs shot with the blaster will be given the "painted" mob effect.
- Added Balloons
  - Balloons are attached to leads by default and can be tied to other mobs and fences.
  - A single Balloon will pull the holder upward slightly. By carrying enough balloons, you can negate fall damage or even begin to float.
  - Balloons can be crafted with phantom membrane, a wind charge, and string
- Added Gravestone Block
  - Zombies will emerge from the ground near the gravestone during the night, during thunderstorms, or in certain spooky biomes.
- Added The Graveyard biome
- Added Garden Gnome Blocks
- Added the Lawn Flamingo Block
- Added The Mail Collection Box
  - Can be triggered with redstone to automatically send mail to a selected mailbox.
- Added Tacos as a food item. It can be received as a raid reward or crafted with ingredients from [Farmer's Delight](https://modrinth.com/mod/farmers-delight-refabricated).
- The Football Helmet now gives increased speed and step height while sprinting. Nearby Mobs will also be knocked back.
- Added "Freeze" Mob Effect
  - Freezes players and mobs in place.
- Added "Chilled" Mob Effect
  - Slows players and mobs down.
- Added "Painted" Mob Effect
  - The effect will cover the player's screen with the dye color of the effect
- Added "Hero of the Garden" Effect
  - While the effect is active, interacting with a mailbox will grant loot for each completed wave in the raid.
### Plants:
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
- Added Winter Melon
  - Acquired from Melon Pult zen-gardening while in cold biomes.
- Added Ice Shroom
  - Found in icy biomes.
- Snow Peas will now apply the Chilled mob effect rather than Slowness and Weakness.
- Added plant transformations
  - Some plants can be transformed into other plants under certain conditions.
- Added Seed Packet Mutations
  - When zen-gardening a plant, seeds for a different plant type may be produced instead.
    - Wall Nut has an 8% chance to produce Explode-O-Nut seeds.
    - Explode-O-Nut has an 40% chance to produce Wall Nut seeds.
    - Electric Peashooter has a 95% chance to produce Peashooter seeds.
    - Peashooter has a 10% chance to produce Repeater seeds. 33% for Fire Peashooter in hot biomes. 40% for Electric Peashooter while thundering.
    - Repeater has a 60% chance to produce Peashooter seeds.
    - Melonpult has a 20% chance to produce Winter Melon seeds in cold biomes.
    - Winter Melon has a 75% chance to produce Melonpult seeds.
- Repeaters no longer spawn naturally.
- Wall Nuts can now be rolled around by either using Gardening Gloves or by being pushed with a piston.
### Zombies:
- Updated the Zombie Raid event.
  - Added new raid music by [@Patrika](https://www.youtube.com/@pattrika)
  - Wild plants within the radius of the raid will vanish, and new plants will not spawn in the area until the raid has been complete.
  - After losing a raid, all the zombies in the raid will vanish and no rewards will be given.
  - The amount of waves a raid is determined by: (A base of 2) + (bad omen level * 2) + (the game's difficulty: 0, 1, 2, or 3) + (2 if the end credits have been seen).
  - Using another Bad Omen potion during a raid will extend the raid by the potion's amplification level. Unless the raid is already in its final wave.
  - After the end credits have been seen, waves during a raid will be more challenging.
  - Added Special wave types:
    - "Bucket Brigade" can occur during waves 1–4.
    - "Half-time Showdown" can occur during waves 2–6.
    - "Winter Wonderland" can occur during waves 5–9.
    - "Pirate Invasion" can occur during waves 9–15.
    - "Robo Army" can occur during waves 12–16. (Only after the end credits have been seen)
    - "League of Awesome" can occur during waves 13–19. (Only after the end credits have been seen)
  - When a raid has been completed, all players participating in the raid will receive the "Hero of the Garden" effect.
  - Special waves add unique loot to the reward item pool when interacting with a mailbox.
  - Every 5 waves completed will add an item from the party loot table.
    - Party Loot Includes:
      - Tacos
      - Balloons
      - Lawn Flamingos
      - Garden Gnomes
- Added Desert, Snow, Pirate, and Balloon Variants for the Browncoat Zombie.
  - The pirate browncoat will spawn during the "Pirate Invasion" special wave.
  - The snow browncoat will spawn during the "Winter Wonderland" special wave.
  - Browncoats that spawn with balloons have a 33% chance to be a balloon browncoat.
  - Browncoats that spawn in the desert have a 75% chance to be a desert browncoat.
- Added a Yeti and Pirate Variants for the Imp,
  - Yeti Imps apply the Chilled mob effect and spawn during the "Winter Wonderland" special wave.
  - Pirate Imps spawn in an explosive barrel and during the "Pirate Invasion" special wave.
- Added a Pirate Variant for Gargantuar that spawns during the "Pirate Invasion" special wave.
- Added Engineer Zombie
  - Will avoid players and plants and will attempt to build zombie bots to attack.
- Added Soldier Zombie
  - Uses the dye blaster.
- Added Grave Digger
  - Each Zombie raid was a 15% chance to spawn exactly one Grave Digger during any wave.
  - Will dig up gravestones while targeting an entity.
  - After the grave digger has dug up 4–6 graves, it will retreat.
- Added Robo Zombie
  - Spawns during the "Robo Army" special wave.
- Added Pirate Captain Zombie
  - Spawns during the "Pirate Invasion" special wave. 
- Added Super Brainz
  - Spawns during the "League of Awesome" special wave.
- Zombies will now attack Wall Nuts when walking into them.
## Technical Changes
- Items that have the Block Projectile item component will no longer deflect piercing projectiles.
- Added BrainzAlloyCost item component.
- Added configuration options.
  - `paz-server.json`:
    - `poweredUpCooldownReduction` – This value will reduce the cooldown for powered up plants. (default is a 20% reduction)
    - `alloyCost` – A list of entity ids paired with an integer.
  - `paz-client.json`:
    - `showHiddenItemsInCreativeTab` – Enables some W.I.P items in the creative tab.
    - `hideRaidUi` – Hides the zombie raid UI.
- Added `seed_mutation` data directory.
  - The path for editing Seed Mutations is: `data/<namespace>/seed_mutation/<entity_name>.json`
  - Entry Format:
    - `mutations` - A list of mutation objects.
      - `biome` - A biome or biome tag list.
      - `weather` - `"clear"`, `"rain"`, `"thunder"`.
      - `time` - `"day"`, `"night"`, or a ranged value: `{ "min": 0, "max": 12000 }`
      - `chance` - value between 0-1. (defaults to 1.0)
      - `result` - An entity id. (Required)
  - Mutation objects are evaluated from top to bottom, the first mutation that matches the conditions will be accepted.
  #### Example file: 
  ```
  {
    "mutations": [
      {
        "weather": "thunder",
        "chance": 0.4,
        "result": "plantz:electric_peashooter"
      },
      {
        "biome": "#plantz:plant/has_fire_peashooter",
        "chance": 0.33,
        "result": "plantz:fire_peashooter"
      },
      {
        "chance": 0.1,
        "result": "plantz:repeater"
      }
    ]
  }
  ```
  - Datapacks or other mods can add or replace these files to change mutation rates and conditions.
- Added `plant_icon` item model type.
  - Used for displaying icons on seed packets and on the gardening glove.
  - Using a resource pack or mod, you can add custom icons for any entity type in the game.
  - Add an icon .png to `assets/<namespace>/models/plant_icon/<entity_name>.png`. And add a model .json to `assets/<namespace>/models/plant_icon/<entity_name>.json`.
  #### Example setup:
  - Model json: `assets/minecraft/models/plant_icon/pig.json`
  ```
  {
    "parent": "minecraft:item/generated",
    "textures": {
      "layer0": "minecraft:item/plant_icon/pig"
    }
  }
  ```
  - Icon png: `assets/minecraft/models/plant_icon/pig.png`
  

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
    - `zenPotTimeReduction` – This value will reduce the final growth time when plants are zen potted. _(1.0 = 100% reduction in sleep needed, aka no sleep.)_
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


