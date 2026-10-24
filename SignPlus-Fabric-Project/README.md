# SignPlus — Fabric client-side project

Targets: Minecraft Java 26.1.2 and 26.3; Java 25; Fabric Loader 0.19.5.

## Intended features
- `/swarp <name>` saves the player's current position locally for the current session.
- `/shome <name>` saves a local home position for the current session.
- Signs with `[warp]`, `[home]`, `[tp]`, or `[https]` in line 1 are handled by the client click hook.
- GitHub Actions builds separate artifacts for 26.1.2 and 26.3.

## Important limitations
This is a client-only mod. It cannot force teleportation on a multiplayer server. Warp/home/tp actions send a normal `/tp` command, which only works if the server allows it. Server-specific warp/home commands are not configured yet. Saved points currently last only until Minecraft closes.

The sign text and mixin signatures can differ between Minecraft 26.1.2 and 26.3. This initial project has not been successfully compiled in this environment; the first GitHub Actions run is required to confirm compatibility and may report API/signature changes. Treat this as a generated starter project, not a verified release JAR.

## Build
Install Java 25 and the matching Gradle version (9.5.1 for 26.1.2; 9.7.1 for 26.3), then run `gradle build`.

## Automatic builds
1. Upload all files to a GitHub repository.
2. Open **Actions** and enable workflows if prompted.
3. Choose **Build SignPlus JARs** → **Run workflow**.
4. Download the matching artifact from the workflow run.

## Sign format
- Line 1 `[warp]`; line 2 warp name
- Line 1 `[home]`; line 2 home name
- Line 1 `[tp]`; line 2 `x y z`
- Line 1 `[https]`; line 2 onward URL
