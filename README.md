# E-Pharmacy

A microservice-based online pharmacy platform — browse the medicine catalog, manage a
cart, place and track orders, and pay, all backed by independent Spring Boot services
behind a Eureka + API Gateway setup, with a React (Vite) frontend.

## Architecture

```
                        ┌───────────────────────┐
                        │  epharmacy-frontend    │
                        │  React + Vite (5173)   │
                        └──────────┬─────────────┘
                                   │ REST (JSON)
                 ┌─────────────────┼─────────────────────────────┐
                 │                 │                              │
        (frontend currently talks directly to each service below,
         the API gateway is available for gateway-routed access too)
                 │                 │                              │
        ┌────────▼───────┐ ┌───────▼────────┐ ┌───────────────────▼──┐
        │ pharmacy-api-   │ │ pharmacy-eureka-│ │  MySQL (localhost)   │
        │ gateway-service │ │ server (8761)   │ │  one DB per service  │
        │ (8080)          │ │ service registry│ │                       │
        └────────┬────────┘ └────────┬────────┘ └───────────────────────┘
                  │                   │
   ┌──────────────┼───────────────────┼──────────────────┬─────────────────┐
   │              │                   │                  │                 │
┌──▼───────────┐┌─▼──────────────┐┌───▼─────────────┐┌───▼──────────────┐┌─▼──────────────────┐
│ user-service ││ medicine-service││ cart-service    ││ order-service    ││ payment-service     │
│ 8081         ││ 8082            ││ 8083            ││ 8084             ││ 8085                │
│ customer_db  ││ medicine_db     ││ cart_db         ││ order_db         ││ payment_db          │
└──────────────┘└─────────────────┘└─────────────────┘└──────────────────┘└─────────────────────┘
```

All backend services register with **Eureka** (`pharmacy-eureka-server`, port `8761`) and
can be reached either directly on their own port or through the **API Gateway**
(`pharmacy-api-gateway-service`, port `8080`), which forwards by path prefix
(`/customer/**`, `/medicine/**`, `/cart/**`, `/order/**`, `/payment/**`) to the matching
service via Eureka's load balancer. The gateway also rate-limits requests via **Redis**
(`redis-rate-limiter`), and the medicine, cart, and order services use the same Redis
instance for response caching — so Redis has to be running alongside MySQL and Eureka,
not just the database.

## Services & ports

| Service                        | Port | Database     | Purpose                                   |
|---------------------------------|------|--------------|--------------------------------------------|
| `pharmacy-eureka-server`        | 8761 | —            | Service registry / discovery              |
| `pharmacy-api-gateway-service`  | 8080 | —            | Single entry point, routes by path prefix, rate limiting |
| `pharmacy-user-service`         | 8081 | `customer_db`| Registration, login, profile, addresses   |
| `pharmacy-medicine-service`     | 8082 | `medicine_db`| Medicine catalog, search, stock           |
| `pharmacy-cart-service`         | 8083 | `cart_db`    | Cart items                                 |
| `pharmacy-order-service`        | 8084 | `order_db`   | Order placement, tracking, cancellation   |
| `pharmacy-payment-service`      | 8085 | `payment_db` | Payment processing, saved cards           |
| `epharmacy-frontend`            | 5173 | —            | React SPA (Vite dev server)                |
| Redis                            | 6379 | —            | Gateway rate limiting; caching for medicine/cart/order |

> The frontend currently calls each backend service directly on its own port (see
> `epharmacy-frontend/src/api/client.js`), the same allow-list approach the backend ships
> with — each service permits `http://localhost:5173` via its own CORS config. The API
> gateway is present and routable but not required for the frontend to work locally.

## Tech stack

**Backend**
- Java 17, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- Spring Web, Spring Data JPA, MySQL 8
- Spring Security + JWT (stateless, `Authorization: Bearer <token>`)
- Spring Cloud Netflix Eureka (discovery), Spring Cloud Gateway **(WebFlux)** for routing
- Redis — gateway rate limiting, response caching on medicine/cart/order services
- ModelMapper, Lombok

**Frontend**
- React 19 + Vite 8
- React Router 7
- Axios

## Prerequisites

- JDK 17+
- Maven (or use the bundled `./mvnw` in each service)
- MySQL 8, with user `root` / password `root` (or update each service's
  `application.yaml`)
- Redis (default port `6379`) — required by the gateway and by the medicine, cart, and
  order services; `docker run -p 6379:6379 redis:alpine` is the easiest way to get one
- Node.js 18+ and npm, for the frontend
- Docker + Docker Compose, if you want to run the whole stack with one command instead
  of starting each piece by hand (see [Running with Docker Compose](#running-with-docker-compose))

> **Heads up on hostnames:** each service's `application.yaml` points its datasource at
> a container hostname (e.g. `jdbc:mysql://user-db:3306/customer_db`), not `localhost`,
> and every service except `pharmacy-eureka-server` requires
> `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` with no built-in default. Both only resolve
> automatically inside the Docker Compose network below — running services individually
> with plain `./mvnw spring-boot:run` needs the overrides shown in
> [Running the backend (without Docker)](#running-the-backend-without-docker).

## Database setup

Each service manages its own schema via `spring.jpa.hibernate.ddl-auto=update`, so you
only need to create the empty databases up front:

```sql
CREATE DATABASE customer_db;
CREATE DATABASE medicine_db;
CREATE DATABASE cart_db;
CREATE DATABASE order_db;
CREATE DATABASE payment_db;
```

## Environment variables
MYSQL_ROOT_PASSWORD=

USER_DB_NAME=
USER_DB_USER=
USER_DB_PASSWORD=

MEDICINE_DB_NAME=
MEDICINE_DB_USER=
MEDICINE_DB_PASSWORD=

CART_DB_NAME=
CART_DB_USER=
CART_DB_PASSWORD=

ORDER_DB_NAME=
ORDER_DB_USER=
ORDER_DB_PASSWORD=

PAYMENT_DB_NAME=
PAYMENT_DB_USER=
PAYMENT_DB_PASSWORD=

API_GATEWAY_URL=http://localhost:8080

DOCKER_USERNAME=

JWT_SECRET=



**`JWT_SECRET`** — all five API services (`pharmacy-user-service`,
`pharmacy-medicine-service`, `pharmacy-cart-service`, `pharmacy-order-service`,
`pharmacy-payment-service`) read a shared JWT signing secret:

```bash
export JWT_SECRET=<any-long-random-string>
```

Set the **same** value before starting every one of them — tokens issued by
`pharmacy-user-service` at login are validated by the other services, so a mismatched
(or missing) secret means every authenticated request fails, and cart/order will
actually refuse to start at all without it.

**`EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`** — every backend service *except*
`pharmacy-eureka-server` itself requires this, with no default value baked in. Point it
at wherever Eureka is reachable from that process, e.g.:

```bash
export EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
```

Without it, each service fails at startup with an unresolved-placeholder error.

## Running with Docker Compose

The repo ships a `Dockerfile` per service plus a root `docker-compose.yml` that builds
and wires up Eureka, Redis, five per-service MySQL containers, all five API services,
the gateway, and the frontend together — this is the actually-complete, no-manual-overrides
way to run the stack.

Create a `.env` file next to `docker-compose.yml` (not included in the repo, since it
holds secrets/credentials) with:

```
DOCKER_USERNAME=<any value used to tag the built images>
MYSQL_ROOT_PASSWORD=<password>
JWT_SECRET=<any long random string>
API_GATEWAY_URL=http://localhost:8080

USER_DB_NAME=customer_db      USER_DB_USER=<user>      USER_DB_PASSWORD=<password>
MEDICINE_DB_NAME=medicine_db  MEDICINE_DB_USER=<user>  MEDICINE_DB_PASSWORD=<password>
CART_DB_NAME=cart_db          CART_DB_USER=<user>      CART_DB_PASSWORD=<password>
ORDER_DB_NAME=order_db        ORDER_DB_USER=<user>     ORDER_DB_PASSWORD=<password>
PAYMENT_DB_NAME=payment_db    PAYMENT_DB_USER=<user>   PAYMENT_DB_PASSWORD=<password>
```

Then:

```bash
docker compose up --build
```

Eureka, Redis, and each MySQL container are health-checked, so dependent services wait
for them before starting. Each per-service MySQL is also published on the host (user
`3307`, medicine `3308`, cart `3309`, order `3310`, payment `3311` → all mapping to
container port `3306`), in case you want to connect to one directly.

## Running the backend (without Docker)

You can also run each service with plain Maven, but since every service's
`application.yaml` points at Docker Compose hostnames (`user-db`, `medicine-db`,
`redis`, etc.) rather than `localhost`, you need to override the datasource URL (and
Eureka zone / JWT secret, per [Environment variables](#environment-variables)) for each
one. Start Eureka first, and have MySQL + Redis already running locally:

```bash
# 1. Service registry
cd pharmacy-eureka-server && ./mvnw spring-boot:run

# 2. Everyone else needs EUREKA_CLIENT_SERVICEURL_DEFAULTZONE; the four services below
#    also need JWT_SECRET, and the four DB-backed ones need SPRING_DATASOURCE_URL
#    pointed at localhost instead of the Docker Compose hostname.
export EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
export JWT_SECRET=<any-long-random-string>

cd pharmacy-api-gateway-service && ./mvnw spring-boot:run

cd pharmacy-user-service     && SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/customer_db ./mvnw spring-boot:run
cd pharmacy-medicine-service && SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/medicine_db ./mvnw spring-boot:run
cd pharmacy-cart-service     && SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/cart_db ./mvnw spring-boot:run
cd pharmacy-order-service    && SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/order_db ./mvnw spring-boot:run
cd pharmacy-payment-service  && SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/payment_db ./mvnw spring-boot:run
```

(Run each in its own terminal so the exported env vars above are inherited by all of
them; JWT_SECRET isn't actually read by the gateway itself, but is required by the
other five.)

Check `http://localhost:8761` — all six services (gateway + five API services) should
show as `UP` in the Eureka dashboard once they've registered.

## Running the frontend

```bash
cd epharmacy-frontend
npm install
npm run dev
```

Opens on `http://localhost:5173`. If any backend service runs on a non-default port,
override it via `.env`:

```bash
VITE_API_GATEWAY_URL=http://localhost:8080
API_GATEWAY_URL=http://localhost:8080

```

## Auth flow

1. `POST /customer/login` (user-service) returns a JWT.
2. The frontend stores it in `localStorage` and attaches it as
   `Authorization: Bearer <token>` on every subsequent request (`src/api/client.js`).
3. Each protected service validates the token independently with the shared
   `JWT_SECRET` — there's no central session store (stateless).
4. If a token is missing/expired, protected endpoints return `401` and the frontend
   automatically clears local auth state and redirects to `/login`.

## Project structure

```
E-Pharmacy-main/
├── pharmacy-eureka-server/        # Service registry
├── pharmacy-api-gateway-service/  # Gateway, routes by path prefix
├── pharmacy-user-service/         # Customers, auth, addresses
├── pharmacy-medicine-service/     # Medicine catalog + static product images
├── pharmacy-cart-service/         # Cart (Feign client → medicine-service)
├── pharmacy-order-service/        # Orders
├── pharmacy-payment-service/      # Payments, saved cards
└── epharmacy-frontend/            # React SPA
    ├── src/api/client.js          # Axios instances + endpoint map
    ├── src/context/               # Auth & cart React contexts
    ├── src/pages/                 # Route-level pages
    └── src/components/            # Shared UI components
```
