# ===== Stage 1: build the React app =====
FROM node:20 AS build
WORKDIR /app

# Install deps from the lockfile first (better layer caching).
COPY package.json package-lock.json ./
RUN npm ci

# Build the production bundle.
COPY . .
RUN npm run build

# ===== Stage 2: serve the static bundle with nginx =====
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/build /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
