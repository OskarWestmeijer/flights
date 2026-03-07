# ---------- Build stage ----------
FROM node:24.14.0-alpine3.23 AS builder

WORKDIR /app

# Install dependencies first (allows better docker layer caching)
COPY package*.json ./
RUN npm ci

# Copy remaining sources required for build
COPY . .
RUN npm run build

# Remove dev dependencies (they were only relevant to run the build)
RUN npm prune --omit=dev


# ---------- Runtime stage ----------
FROM node:24.14.0-alpine3.23

LABEL Developers="Oskar Westmeijer"

WORKDIR /app

# Copy only runtime artifacts
COPY --from=builder --chown=node:node /app/build ./build
COPY --from=builder --chown=node:node /app/node_modules ./node_modules
COPY --from=builder --chown=node:node /app/package.json ./package.json

# official node image non-root user
USER node

CMD ["sh", "-c", "echo \"[INFO] Flights-UI Container started\" && node build/index.js"]