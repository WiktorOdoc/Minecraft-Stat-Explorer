# Minecraft Statistics Explorer

Small desktop app for browsing Minecraft player statistics from a world save.

## What it does
- Reads `UUID.json` stat files from the `stats/` directory.
- Resolves player nicknames through the Mojang API.
- Shows per-category tables, top records, and per-player detail windows.

## Project structure
- `src/main/java/com/wt/mse/services` - loading, parsing, formatting, and orchestration logic.
- `src/main/java/com/wt/mse/gui` - Swing windows and table rendering.
- `src/main/java/com/wt/mse/model` - data objects.
- `src/test/java/com/wt/mse/services` - JUnit tests for parsing and service logic.

## Requirements
- Java 22
- Internet access for nickname lookup

## Run locally
```bash
mvn test
```

To launch the app, run the `com.wt.mse.Main` class from your IDE or build a jar with Maven.

## Notes
- The app expects a `stats/` folder next to the working directory.
- The repo now has GitHub Actions configured to run `mvn test` on push and pull request.
