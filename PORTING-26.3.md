# Minecraft 26.3 (Fabric)

This port targets Minecraft Java Edition 26.3 and is based on upstream `main`
through `ddf05fb7`. It retains the current raid, spawning, loot, and save fixes.

## Requirements and build

- Java 25
- Fabric Loader 0.19.5 or later
- Fabric API 0.162.0+26.3 or later for Minecraft 26.3
- Fabric Language Kotlin 1.13.8+kotlin.2.3.0 or later
- TerraBlender Fabric 26.3-26.3.0.0.9 (included as an unmodified nested mod)
- Optional Iris: compilation uses 1.11.7+26.3-fabric

Run `./gradlew build --console=plain` (Windows: `./gradlew.bat build --console=plain`).
The output is `build/libs/plants-and-zombies-1.5+mc26.3.jar`.

TerraBlender's nested libraries and LGPLv3 metadata are preserved. Upstream source:
https://github.com/Glitchfiend/TerraBlender. The single `include` dependency in
`build.gradle` controls bundling; `fabric.mod.json` declares TerraBlender as required.

## Compatibility changes

- Update Loom and Gradle for 26.3, keeping Java 25 and Kotlin 2.3.0.
- Adapt advancement registries and predicates, generic movement controls, piston
  reactions, knockback damage context, swings, and entity interpolation.
- Replace block codecs removed by vanilla and migrate fuel/composting components.
- Update RenderPearl pipeline bindings, shader interfaces, pose rotations, model
  submissions, breaking overlays, camera access, music toasts, and the Hud mixin.
- Migrate loot conditions/modifiers, entity predicates, number providers, recipe
  triggers, villager trades, structure spawn overrides, and biome spawn attributes.
- Move configured features to `worldgen/feature`, update block state/provider
  formats and placement offsets, and use TerraBlender material rules.
- Preserve digger-breakable materials without referencing removed carver tags.
- Fix the Brainzium advancement's reference to a nonexistent recipe. It now checks
  `plantz:brainz_alloy` and retains its inventory criterion and both recipe rewards.

## Validation

`validatePortResources` runs as part of `check` and `build`, without starting a
client/server or loading a world. It checks Minecraft 26.3 resource codecs,
required nested tag references, local dynamic-registry resource references,
advancement parents/rewards, and obsolete loot/spawn keys. It currently checks
381 resources/advancement criteria, including all four structure overrides.

Vanilla stand-ins are used for mod values, and custom item components are excluded.
These checks do not verify code-registered item/block/entity identities, complete
resolved tag contents, visual output, or gameplay.

Client startup and world loading were manually confirmed for the v1.5-based port
on Minecraft 26.3 with Loader 0.19.5 and API 0.162.0+26.3. The current upstream-based
revision has passed the build and data checks, but has not been rerun in-game.
Dedicated server, save/reload, world generation, raids, plant behavior, and
rendering with/without Iris still need broader manual coverage.
