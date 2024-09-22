# Flights-ui

## build and test

```bash
npm install
npm run build

# will quit vitest once done
npm run test:unit run
```

## local development

```bash
# ensure the backend is started
npm run dev
```

## updating dependencies

```bash
npm install -g npm-check-updates
ncu

# granular updates
ncu -u --target=patch
ncu -u --target=minor

ncu -u
```
