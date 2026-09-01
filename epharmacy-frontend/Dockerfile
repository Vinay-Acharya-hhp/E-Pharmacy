# ---- Build stage -----------------------------------------------------------
FROM node:20-alpine AS build
WORKDIR /app

# Install deps first so this layer is cached unless package*.json changes
COPY package.json package-lock.json* ./
RUN npm ci

COPY . .

# Backend service URLs — Vite bakes these into the JS bundle at build time,
# NOT read at container start. Pass a different value per environment via
# `docker build --build-arg VITE_MEDICINE_URL=... ...`
ARG VITE_API_GATEWAY_URL

ENV VITE_MEDICINE_URL=$VITE_API_GATEWAY_URL 

RUN npm run build

# ---- Serve stage ------------------------------------------------------------
FROM nginx:1.27-alpine AS serve

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

# Run as non-root
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    touch /var/run/nginx.pid && \
    chown -R nginx:nginx /var/run/nginx.pid
USER nginx

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
