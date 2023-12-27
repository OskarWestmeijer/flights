# maps

![main branch](https://github.com/OskarWestmeijer/maps/actions/workflows/main-build-test-release.yml/badge.svg)
[![codecov](https://codecov.io/gh/OskarWestmeijer/maps/graph/badge.svg?token=EHEHAUD5DM)](https://codecov.io/gh/OskarWestmeijer/maps)
[![Better Stack Badge](https://uptime.betterstack.com/status-badges/v1/monitor/vmxk.svg)](https://uptime.betterstack.com/?utm_source=status_badge)

Displays Hamburg airport connections on a 3d globe. The backend provides a public Api.

- [https://maps.oskar-westmeijer.com](https://maps.oskar-westmeijer.com) (Sveltekit frontend)
- [https://api.maps.oskar-westmeijer.com](https://api.maps.oskar-westmeijer.com) (Ktor backend)

## Technologies

```
- Kotlin & Ktor
- Typescript & Sveltekit
- GlobeJs, Tailwindcss & DaisyUI
```

## Build & Test

``` bash
docker compose up -d
./gradlew clean check
```

## How-to Run

Execute these commands and refer to the `frontend/README.md` for further instructions.

``` bash
docker compose up -d
./gradlew run
```