# Flights-ui

## build and test

```bash
npm install
npm run build

# unit-test (vitest)
npm run test:unit
```

### Playwright e2e test

I develop on Linux Fedora, which does not natively support playwright. Use distrobox. 


#### Prerequisites
```bash
sudo dnf install distrobox
mkdir ~/distrobox
distrobox create \
--name ubuntu --image ubuntu:24.04 \
--home ~/distrobox/ubuntu \
--additional-packages "git vim nodejs npm"
```

#### Test execution
```bash
docker compose up -d
distrobox enter ubuntu
npx playwright install --with-deps
npm run test:e2e
```

## local development

### using Wiremock Backend

```bash
docker compose up -d
npm run dev
```

### using Locally started Backend

```bash
# start the backend applications as described in the projects root README
npm run dev
```

## Update dependencies

Ensure the tool `ncu` is installed `npm install -g npm-check-updates`.

```bash
# list possible updates
ncu

# granular updates
ncu -u --target=patch
ncu -u --target=minor

# major updates
ncu -u
```

### Clear Node Modules and Cache

Updating can run into issues. These commands clear the dependencies for a fresh installment.

```bash
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```
