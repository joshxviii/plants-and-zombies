# Working on this fork

- Read `PORTING.md` before updating a Minecraft port. Inspect the current branch,
  working tree, remotes and upstream baseline before editing.
- `main` is reserved for the upstream mirror. Upstream renamed its version
  branches and now defaults to `26.3`; choose the mirror source only during an
  explicitly requested sync. Add fork changes to a Minecraft port branch;
  never merge port commits or fork-only docs into main.
- Maintain `codex/fabric-26.2-v1.5` for Minecraft 26.2 and
  `codex/fabric-26.3-upstream` for Minecraft 26.3. Keep the existing branches
  when importing later mod releases, and update the documented baseline/version.
- Each port has its own maintenance chat. This branch belongs to the Minecraft
  26.3 chat; leave 26.2 maintenance to its chat unless the user asks otherwise.
  Import upstream changes when requested; do not automatically change targets.
- The current working 26.3 baseline is `1.6-dev.fbc1a3b6.compat1+mc26.3`, runtime
  source `63415ad4`, upstream `26.3` at `fbc1a3b6`. The user confirmed it works
  on 2026-11-07 and selected it for continued maintenance on this same branch.
  Preserve the TerraBlender callback initialization fix during future imports.
- Chats sharing a checkout also share its active branch. Check it before every
  update and do not switch a checkout another chat is using. Use separate Git
  worktrees when the two chats work concurrently.
- Preserve target-version configuration and API/resource adaptations during
  upstream merges. Do not copy the other port's code or data changes blindly.
- Build with Java 25 and `./gradlew.bat build --console=plain` (Windows) or
  `./gradlew build --console=plain` (Linux). Run `git diff --check`, inspect the
  versioned JAR metadata and bundled TerraBlender, and keep generated files out
  of commits. Preserve user files, worlds and untracked data.
- Routine verification is build/data validation. Do not launch Minecraft or run
  `runClient` unless the user explicitly asks. Attribute reported gameplay
  results to the user and the version actually tested.
- Update the fork section of `CHANGELOG.md`, README and `PORTING.md` when the
  baseline, dependencies, installation requirements or validation status change.
- Keep the current 26.2 and 26.3 runtime/source JARs together in the designated
  `build/libs` output folder. Do not erase the other port with `clean`; if a clean
  build is needed, isolate it or preserve and restore the other port's artifacts.
  Remove obsolete builds only from the folder/version the user requests.
- Push only when requested. When publishing, verify the destination branch and
  exact commit, then inspect CI for that commit. Keep the historical archive.
