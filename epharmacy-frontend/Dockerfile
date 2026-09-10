# ---- Build stage -----------------------------------------------------------
FROM node:20-alpine AS build
WORKDIR /app

# Install deps first so this layer is cached unless package*.json changes
COPY package.json package-lock.json* ./
RUN npm ci

COPY . .

# Backend API gateway URL — Vite bakes this into the JS bundle at build
# time, NOT read at container start.
# Default "/api" is a relative path: nginx (see nginx.conf.template)
# proxies it to the gateway, so no node IP/NodePort/absolute host is ever
# baked into the bundle. For local `npm run dev` against services on your
# own machine, override with:
#   docker build --build-arg VITE_GATEWAY_URL=http://localhost:8080 ...
ARG VITE_GATEWAY_URL=/api
ENV VITE_GATEWAY_URL=$VITE_GATEWAY_URL
RUN npm run build

# ---- Serve stage ------------------------------------------------------------
FROM nginx:1.27-alpine AS serve

# Copied into /etc/nginx/templates/ (not /etc/nginx/conf.d/ directly) so
# nginx's own docker-entrypoint.d/20-envsubst-on-templates.sh substitutes
# ${GATEWAY_UPSTREAM} and ${RESOLVER} from environment variables into the
# real config at container startup — every time the container starts,
# not just at build time. This is what lets the exact same image serve
# both Docker Compose and Kubernetes correctly: each environment just
# sets these two env vars differently; the image itself never changes.
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

# Defaults matched to Docker Compose's service names/networking
# (see docker-compose.yaml: services.api-gateway, and Docker's embedded
# DNS at 127.0.0.11). Kubernetes overrides both of these at the
# Deployment level (via the web-nginx-env ConfigMap) to point at
# api-gateway-service and the cluster's CoreDNS ClusterIP instead —
# no rebuild needed for that environment switch.
ENV GATEWAY_UPSTREAM=api-gateway:8080
ENV RESOLVER=127.0.0.11

COPY --from=build /app/dist /usr/share/nginx/html

# Run as non-root. /etc/nginx/conf.d must be writable by the nginx user
# too — the entrypoint's envsubst step writes the substituted
# default.conf.template output there at container startup. Without this,
# that step fails silently (logs an error but doesn't stop the
# container), and nginx falls back to its stock default.conf from the
# base image instead of ours — which is exactly what caused the earlier
# 404s (they were nginx's built-in welcome-page config, not a bug in the
# template itself).
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    chown -R nginx:nginx /etc/nginx/conf.d && \
    touch /var/run/nginx.pid && \
    chown -R nginx:nginx /var/run/nginx.pid

USER nginx
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

