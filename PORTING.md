# Maintaining the Minecraft ports

## Branch structure

| Branch | Purpose |
| --- | --- |
| [`main`](https://github.com/Mikkelka/plants-and-zombies/tree/main) | Reserved for the upstream mirror (previous upstream source: `main`). Sync separately when requested; keep fork changes and port merges on the port branches. |
| [`codex/fabric-26.2-v1.5`](https://github.com/Mikkelka/plants-and-zombies/tree/codex/fabric-26.2-v1.5) | Maintained Minecraft 26.2 port. Last observed baseline `afa14f39` (1.5.2), port commit `c083f699`; maintained by its own chat. |
| [`codex/fabric-26.3-upstream`](https://github.com/Mikkelka/plants-and-zombies/tree/codex/fabric-26.3-upstream) | Maintained Minecraft 26.3 port. Current upstream baseline: `26.3` at `fbc1a3b6` (2026-11-07), including 1.6 development. |

Upstream has replaced `main` with version branches; its current default is
`26.3`, and `26.1` remains separate. The fork's `main` was not changed by this
26.3 import. Do not automatically repoint or reset the mirror when upstream
renames branches; choose its intended source during a separately requested sync.
Keep historical archives, including the local `archive/26.3-compat3` tag.

Use the two port branches for ongoing work, including later mod releases. The
`v1.5` suffix in the 26.2 branch name records its starting point; the current mod
version comes from `gradle.properties`. Do not merge either port back into main
or copy Minecraft-specific changes wholesale between the ports.

## Separate maintenance chats

Each Minecraft version has its own maintenance chat:

- The 26.3 chat maintains `codex/fabric-26.3-upstream` and keeps Minecraft 26.3.
- The 26.2 chat maintains `codex/fabric-26.2-v1.5` and keeps Minecraft 26.2.

The current working baseline for this chat is
`1.6-dev.fbc1a3b6.compat1+mc26.3`, with runtime source at `63415ad4` and upstream
baseline `fbc1a3b6`. The user selected this as the ongoing 26.3 version on
2026-11-07 after confirming that it works. Future updates continue from this
branch and preserve its compatibility changes, especially the TerraBlender
callback. This designation does not rename the branch or change Git's `main`.

When the original project publishes changes, the user asks each chat separately
to import the selected update into its existing branch. Upstream changes do not
automatically update either port, and a new mod release does not change the
branch's Minecraft target. `main` remains the upstream mirror.

Before editing or building, check the actual branch, working tree and target
version. Two chats using the same project directory share one active checkout;
their branch selections are not isolated. Use separate Git worktrees for
concurrent work, and do not switch a checkout another chat is actively using.

For each requested update, follow the upstream-import and validation steps below,
update the version/baseline records, and report the target Minecraft version,
imported upstream commit, mod version, validation performed and output JAR.
Commit to the intended port branch and push when requested. Continue using an
existing upstream PR for that branch rather than opening a new PR for each fix.

## Current versions and validation

| | Minecraft 26.2 port | Minecraft 26.3 port |
| --- | --- | --- |
| Mod version | `1.5.2+mc26.2` (last observed) | `1.6-dev.fbc1a3b6.compat1+mc26.3` |
| Java | 25 | 25 |
| Gradle wrapper / Loom | 9.6.1 / 1.17.17 | 9.8.0 / 1.19.3 |
| Fabric Loader used to build | 0.19.3 | 0.19.5 |
| Fabric API used to build | `0.156.0+26.2` | `0.162.0+26.3` |
| TerraBlender (bundled) | `26.2-26.2.0.0.2` | `26.3-26.3.0.0.9` |

On 2026-10-25 the user confirmed that the Minecraft 26.2 build works in game.
The build also passed resource validation (377 resources/criteria) and a static
check of 40 Mixin member, descriptor and instruction references. Specific
multiplayer or saved-world migration scenarios were not separately recorded.
This report applies to the 26.2 port; it does not establish gameplay validation
for the independently maintained 26.3 port.

The 26.3 branch was rebuilt on 2026-11-07 after importing upstream `26.3` at
`fbc1a3b6`. The build, 464 resource/criterion checks and JAR inspection passed, with no Minecraft
launch or gameplay test. Existing 26.2 artifacts were preserved. See
`PORTING-26.3.md` for the snapshot contents and compatibility fixes.

The user's subsequent startup test exposed premature TerraBlender registration
in the first snapshot (`TerraBlender.CONFIG` was null). `compat1` moves it to the
`terrablender` entrypoint. Its build/data checks passed; the corrected build
was then confirmed working by the user on 2026-11-07 in the Modrinth test
profile with Minecraft 26.3, Loader 0.19.5 and Fabric API 0.162.0+26.3.
This confirms the reported startup problem is resolved. Dedicated-server,
save migration, raids and other broad gameplay coverage were not separately
reported. `compat1` is the current working 26.3 baseline.

Upstream's imported translations still contain missing/unused keys relative to
the 449-key English file. The separate language check reports these differences;
they are not a failure of the build/resource validation. See `PORTING-26.3.md`.

The 26.2 branch was separately updated to 1.5.2 by its maintenance chat.
This import changes only 26.3; updating either port does not update the other.

## Installing a port

Use the JAR for your exact Minecraft version. Install Fabric Loader, the
matching Fabric API and Fabric Language Kotlin. TerraBlender is bundled as its
original nested JAR, including its own metadata and nested libraries.
`iris_version` is an optional compile-only API dependency; Iris is not required
for ordinary play and is not bundled. Shader compatibility needs a separate
check with an Iris version intended for the target Minecraft version.

Do not load both port JARs together: they have the same mod ID, `plantz`.
A successful GitHub Actions build provides JARs under that run's **Artifacts**;
download the mod JAR, not the `-sources.jar`.

## Taking an upstream update

Start with a clean working tree, verify the assigned port branch and preserve
user files. Fetch the available updates:

```powershell
git fetch origin
git fetch upstream --tags
```

Resolve the actual upstream branch first (`git ls-remote --symref upstream HEAD`).
As of 2026-11-07 it is `26.3`; `upstream/main` no longer exists. The example
mirror commands below apply only when upstream actually has a `main` branch.

Import the selected upstream ref directly into the assigned port branch. Syncing
the `main` mirror is a separate action when requested; it is not a prerequisite
for updating a port. If syncing the mirror, use a checkout that no other chat is
using and verify `main` is still an upstream-only mirror:

```powershell
git switch main
git merge --ff-only upstream/main
git push origin main
```

In the port's own checkout, review and merge the chosen
upstream release tag or commit into that branch; do not automatically take all
of upstream main when the intended base is a published release. Record the
selected upstream baseline in this document and the fork section of
`CHANGELOG.md`.

When resolving conflicts, preserve the target Minecraft version, compatible
Fabric/Loom/Gradle/TerraBlender dependencies and `fabric.mod.json` version range.
Recheck movement controls, advancement predicates, rendering, knockback,
world generation and loot formats against the target Minecraft API. Keep
upstream gameplay and assets unless a compatibility change requires otherwise.
Minecraft 26.3 resource formats are not interchangeable with 26.2 formats.

Set `mod_version` to the imported upstream version with `+mc26.2` or `+mc26.3`.
Update the version table and README when dependencies or the baseline change.
Do not overwrite an already distributed version with materially different code;
use an additional compatibility/build suffix when needed.

## Building and checking a port

Use Java 25 and the wrapper from the selected branch:

```powershell
./gradlew.bat build --console=plain
git diff --check
```

On Linux use `./gradlew build --console=plain`. Outputs are in `build/libs/`:

- 26.2: `plants-and-zombies-1.5.2+mc26.2.jar` (last observed)
- 26.3: `plants-and-zombies-1.6-dev.fbc1a3b6.compat1+mc26.3.jar`

Names above describe the current versions. `build/libs/` may still contain older
builds, so check the versioned filename and the embedded `fabric.mod.json`.
Inspect its Minecraft range, entrypoints and nested TerraBlender JAR before
publishing. Keep build output out of Git; use CI artifacts for downloads.

For local delivery, keep the latest runtime JAR and matching `-sources.jar` for
each of 26.2 and 26.3 together in the designated `build/libs` folder. Rebuilding
one port must preserve the other port's files. Do not run an unconditional
`clean` there; use an isolated build directory or preserve and restore the other
version first. When cleanup is requested, remove only obsolete files for the
specified version/folder, after the replacement build has passed its checks.

`build` runs `validatePortResources`, which decodes world generation, loot
tables, recipes, trades, predicates and advancement criteria with that branch's
Minecraft codecs. It also checks required tags and local data references.
Mod registry values use vanilla stand-ins and custom item components are
excluded, so it does not verify every mod identity or component value.

Do not start Minecraft or run `runClient` as part of routine build validation
unless the user asks. Record in-game confirmation separately from build/data
validation, and retain which port/build was tested.

Commit and push only the intended branch, then inspect the GitHub Actions build
for that exact commit. Treat a queued build as pending and a completed failed
build as a failure. Never merge a port into main to publish it.
