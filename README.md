# E-Pharmacy

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
service via Eureka's load balancer.

## Services & ports

| Service                        | Port | Database     | Purpose                                   |
|---------------------------------|------|--------------|--------------------------------------------|
| `pharmacy-eureka-server`        | 8761 | —            | Service registry / discovery              |
| `pharmacy-api-gateway-service`  | 8080 | —            | Single entry point, routes by path prefix |
| `pharmacy-user-service`         | 8081 | `customer_db`| Registration, login, profile, addresses   |
| `pharmacy_medicine_service`     | 8082 | `medicine_db`| Medicine catalog, search, stock           |
| `pharmacy-cart-service`         | 8083 | `cart_db`    | Cart items                                 |
| `pharmacy-order-service`        | 8084 | `order_db`   | Order placement, tracking, cancellation   |
| `pharmacy-payment-service`      | 8085 | `payment_db` | Payment processing, saved cards           |
| `epharmacy-frontend`            | 5173 | —            | React SPA (Vite dev server)                |

> The frontend currently calls each backend service directly on its own port (see
> `epharmacy-frontend/src/api/client.js`), the same allow-list approach the backend ships
> with — each service permits `http://localhost:5173` via its own CORS config. The API
> gateway is present and routable but not required for the frontend to work locally.

## Tech stack

**Backend**
- Java 17, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- Spring Web, Spring Data JPA, MySQL 8
- Spring Security + JWT (stateless, `Authorization: Bearer <token>`)
- Spring Cloud Netflix Eureka (discovery), Spring Cloud Gateway (WebMVC) for routing
- ModelMapper, Lombok

**Frontend**
- React 19 + Vite 8
- React Router 7
- Axios

## Prerequisites

- JDK 17+
- Maven (or use the bundled `./mvnw` in each service)
- MySQL 8 running on `localhost:3306` with user `root` / password `root` (or update each
  service's `application.yaml`)
- Node.js 18+ and npm, for the frontend

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

The user, medicine, and payment services read a shared JWT signing secret:

```bash
export JWT_SECRET=<any-long-random-string>
```

Set the **same** value before starting every service — tokens issued by
`pharmacy-user-service` at login are validated by the other services, so a mismatched
secret means every authenticated request fails.

## Running the backend

Start the services in this order (Eureka first, gateway can come up anytime after):

```bash
# 1. Service registry
cd pharmacy-eureka-server && ./mvnw spring-boot:run

# 2. API gateway
cd pharmacy-api-gateway-service && ./mvnw spring-boot:run

# 3. Core services (any order, each in its own terminal)
cd pharmacy-user-service        && JWT_SECRET=$JWT_SECRET ./mvnw spring-boot:run
cd pharmacy_medicine_service    && JWT_SECRET=$JWT_SECRET ./mvnw spring-boot:run
cd pharmacy-cart-service        && ./mvnw spring-boot:run
cd pharmacy-order-service       && ./mvnw spring-boot:run
cd pharmacy-payment-service     && JWT_SECRET=$JWT_SECRET ./mvnw spring-boot:run
```

Check `http://localhost:8761` — all five services should show as `UP` in the Eureka
dashboard once they've registered.

## Running the frontend

```bash
cd epharmacy-frontend
npm install
npm run dev
```

Opens on `http://localhost:5173`. If any backend service runs on a non-default port,
override it via `.env`:

```bash
VITE_CUSTOMER_URL=http://localhost:8081
VITE_MEDICINE_URL=http://localhost:8082
VITE_CART_URL=http://localhost:8083
VITE_ORDER_URL=http://localhost:8084
VITE_PAYMENT_URL=http://localhost:8085
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
├── pharmacy_medicine_service/     # Medicine catalog + static product images
├── pharmacy-cart-service/         # Cart (Feign client → medicine-service)
├── pharmacy-order-service/        # Orders
├── pharmacy-payment-service/      # Payments, saved cards
└── epharmacy-frontend/            # React SPA
    ├── src/api/client.js          # Axios instances + endpoint map
    ├── src/context/               # Auth & cart React contexts
    ├── src/pages/                 # Route-level pages
    └── src/components/            # Shared UI components
```
