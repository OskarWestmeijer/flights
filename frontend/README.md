# Flights-ui

## build and test

```bash
npm install
npm run build

# will quit vitest once done
npm run test
```

## local development

### Wiremock Backend
```bash
docker compose up -d
npm run dev
```

### Locally started Backend

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