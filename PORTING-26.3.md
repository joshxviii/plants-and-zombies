# Minecraft 26.3 (Fabric)

This port targets Minecraft Java Edition 26.3 and is based on upstream `26.3`
through `fbc1a3b6fc31a891e5aace3b19b03b9482bdff37` (2026-11-07).
It imports 262 commits since the previous baseline `ddf05fb7`, including
1.5.1/1.5.2 and ongoing 1.6 development (Citron and Time Machine/dimensions).
Upstream still declares `mod_version=1.5.2`; the mixed development snapshot is
explicitly named `1.6-dev.fbc1a3b6.compat1+mc26.3` here, rather than a stable 1.6 release.

## Maintenance purpose

`codex/fabric-26.3-upstream` is the ongoing Minecraft 26.3 maintenance branch.
The current working baseline is `1.6-dev.fbc1a3b6.compat1+mc26.3`, whose runtime
source is commit `63415ad4`. The user confirmed it works and selected it for
continued maintenance on 2026-11-07. Use it in preference to the initial
`fbc1a3b6` snapshot and the older v1.5 port; keep the versioned branch name.
The chat assigned to 26.3 imports requested updates from the original project
while preserving this Minecraft target and its compatible dependencies. The
26.2 chat independently maintains `codex/fabric-26.2-v1.5`.

See [PORTING.md](PORTING.md#separate-maintenance-chats) for chat ownership,
shared-checkout precautions, upstream updates and artifact handling. Keep the
same branch for future mod releases and update the recorded upstream baseline,
mod version and validation results after each requested update.

## Requirements and build

- Java 25
- Fabric Loader 0.19.5 or later
- Fabric API 0.162.0+26.3 or later for Minecraft 26.3
- Fabric Language Kotlin 1.13.8+kotlin.2.3.0 or later
- TerraBlender Fabric 26.3-26.3.0.0.9 (included as an unmodified nested mod)
- Optional Iris: compilation uses 1.11.7+26.3-fabric

Run `./gradlew build --console=plain` (Windows: `./gradlew.bat build --console=plain`).
The output is `build/libs/plants-and-zombies-1.6-dev.fbc1a3b6.compat1+mc26.3.jar`.

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

The latest import uses the original project's own 26.3 source/rendering
adaptations in place of our earlier equivalents. Fork-specific changes retain
the existing Gradle/Loom/Kotlin/Fabric versions and bundled TerraBlender:

- Register flag/battery points of interest with Fabric `PoiHelper`, avoiding
  upstream's call to a private Minecraft method.
- Migrate residual legacy loot keys in six tables, including the door's
  `match_block` condition, preserving drop quantities and probabilities.
- Move natural spawns/costs into biome attributes and convert spawn counts in
  the five new time-travel/gnome biomes.
- Keep Mod Menu optional (`compileOnly` 21.0.0) and its new entrypoint.
- Extend validation for the custom door-shield recipe, boss-defeat criterion,
  door block-state stand-in and the unit-valued lobbing enchantment effect.
- In `compat1`, run biome/material-rule registration from the `terrablender`
  entrypoint implementing `TerraBlenderApi.onTerraBlenderInitialized`, rather
  than the mod's `main` entrypoint. The first snapshot accessed `Regions`
  before TerraBlender had set its configuration, crashing with a null config
  and preventing other integrations (including Biomes O' Plenty) from loading.

## Validation

`validatePortResources` runs as part of `check` and `build`, without starting a
client/server or loading a world. It checks Minecraft 26.3 resource codecs,
required nested tag references, local dynamic-registry resource references,
advancement parents/rewards, and obsolete loot/spawn keys. It currently checks
464 resources/advancement criteria. Upstream removed the old four
vanilla structure overrides; those obsolete overrides are no longer bundled.

Latest local rebuild: 2026-11-07, Java 25, using this branch without `clean`.
The build, data checks and packaged-JAR inspection passed. Existing 26.2 JARs
were preserved. See `PORTING.md` for the validation count.

Vanilla stand-ins are used for mod values, and custom item components are excluded.
The custom lobbing effect's unit payload is checked directly and excluded from
vanilla's effect registry lookup. These checks do not verify code-registered item/block/entity identities, complete
resolved tag contents, visual output, or gameplay.

Client startup and world loading were manually confirmed for the v1.5-based port
on Minecraft 26.3 with Loader 0.19.5 and API 0.162.0+26.3. The user tested the first
`fbc1a3b6` snapshot on 2026-11-07 and reported the TerraBlender startup crash
described above. `compat1` has passed the build/data checks and packaged
entrypoint/bytecode inspection. The user then confirmed that `compat1` works
in the Modrinth test profile with Loader 0.19.5 and API 0.162.0+26.3 on
2026-11-07, resolving the reported startup crash. This is user-reported
successful startup/use; no additional gameplay coverage was specified.
Dedicated server, save/reload, world generation, raids, plant behavior, and
rendering with/without Iris still need broader manual coverage.

The imported translation files match upstream `fbc1a3b6` unchanged. Their keys
are not fully synchronized with the 449-key English file; the separate language
CI check reports missing/unused keys. This inherited translation limitation is
independent of the passing build/resource checks and the user's startup report.
