FROM node:22.11.0-alpine3.20

LABEL Developers="Oskar Westmeijer"

WORKDIR /app

COPY . .

RUN npm ci
RUN npm run build

# Delete source code files that were used to build the app that are no longer needed
RUN rm -rf src/ static/

USER node:node

CMD ["node","build/index.js"]