# spotube-plugin-spotify

## Build & Test

```bash
# Build Zipine (.zipline) target - use for checking errors and build failures
./gradlew :spotube_plugin_spotify:compileDevelopmentExecutableKotlinJsZipline

# JS Build
./gradlew :spotube_plugin_spotify:compileDevelopmentExecutableKotlinJs

# Run tests
./gradlew :spotube_plugin_spotify:jsTest
```

## Architecture

- **Kotlin Multiplatform** — JS-only target (`browser()`), compiled to Zipline plugin
- **Single module**: `spotube_plugin_spotify`
- **Main entrypoint**: `spotube_plugin_spotify/src/jsMain/kotlin/io/github/sonic_liberation/spotube_plugin_spotify/js.kt` — binds `CoreAPI` and metadata APIs to Zipline
- **Service names** follow `*_SERVICE_NAME` constants from `dev.krtirtho.spotube:plugin_interfaces`
- **Configuration cache enabled** by default (`org.gradle.configuration-cache=true` in `gradle.properties`); some tasks (e.g. publish) require `--no-configuration-cache`

## License & Pre-commit

- **AGPL-licensed** — all source files must retain the AGPL header (see `.github/agpl_header.txt`)
- Pre-commit hook (lefthook) runs `addlicense` on staged `*.kt`, `*.kts`, `*.xml`, `*.yaml`, `*.yml`, `*.toml` files
- Use `addlicense -f .github/agpl_header.txt {staged_files}` to fix missing headers

## Publishing

- **MavenLocal**: `./gradlew :spotube_plugin_spotify:publishToMavenLocal` → `~/.m2/repository/io/github/sonic_liberation/spotube_plugin_spotify/`
- **MavenCentral**: Requires Sonatype account, GPG key, and credentials in `gradle.properties`. Run with `--no-configuration-cache`

## Dependencies

- `dev.krtirtho.spotube:plugin_interfaces` — plugin host API interfaces
- `app.cash.zipline:zipline` — Kotlin/JS plugin runtime
- `dev.whyoleg.cryptography` — cryptographic operations (TOTP)
