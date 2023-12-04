# maps

![main branch](https://github.com/OskarWestmeijer/maps/actions/workflows/main-build-test-release.yml/badge.svg)
[![Better Stack Badge](https://uptime.betterstack.com/status-badges/v1/monitor/vmxk.svg)](https://uptime.betterstack.com/?utm_source=status_badge)

Displays Hamburg airport connections on a 3d globe.

- [https://maps.oskar-westmeijer.com](https://maps.oskar-westmeijer.com) (Sveltekit frontend)
- [https://api.maps.oskar-westmeijer.com](https://api.maps.oskar-westmeijer.com) (Ktor backend)

## Technologies

```
- Kotlin & Ktor
- Typescript & Sveltekit
- GlobeJs
```

## Build & Test

### Backend

``` bash
./gradlew clean test
./gradlew clean build
```

### Frontend

``` bash
cd frontend
npm install
npm run test
```

## How-to Run

### Backend

``` bash
./gradlew run
```

### Frontend

``` bash
cd frontend
npm run dev
```
