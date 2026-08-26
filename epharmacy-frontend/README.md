# MedRx — E-Pharmacy Frontend

A React (Vite) frontend for the [E-Pharmacy](https://github.com/Vinay-Acharya-hhp/E-Pharmacy) Spring Boot microservices backend.

## What's included

- **Catalog** — browse, search, and filter medicines by category, with pagination
- **Medicine photos** — each medicine card shows its `imageUrl` photo when the backend has one, and falls back to generated label art (colored monogram) when it doesn't
- **Auth** — register / sign in against `pharmacy-user-service` (JWT stored client-side)
- **Cart** — add, update quantity, remove, and clear items via `pharmacy-cart-service`

## Backend change required

The original `Medicine` entity/DTOs had no photo field. This project adds one:

```java
// Medicine.java, MedicineRequestDTO.java, MedicineResponseDTO.java
private String imageUrl;
```

This has already been applied to the cloned backend copy used to build this frontend — apply the same one-line addition (plus a getter/setter, handled by Lombok) to your own copy of `pharmacy_medicine_service` if you haven't already. `ModelMapper` maps it automatically since the field name matches across entity/DTOs; no other backend code needs to change.

When adding a medicine via `POST /medicine/add-medicine`, just include an `imageUrl` pointing at any publicly reachable image (a CDN link, an uploaded image host, etc.) — this project doesn't include file upload/storage, only display.

## Running it

1. Start the backend services (Eureka, gateway, and the five microservices) as usual, with a running MySQL instance.
2. In this folder:
   ```bash
   npm install
   npm run dev
   ```
3. Open http://localhost:5173

The frontend calls each microservice **directly on its own port** (8081–8085), not through the gateway — this matches the CORS configuration already present in each service (`http://localhost:5173` is allow-listed per-service, but the gateway itself has no CORS config). If your ports differ, copy `.env.example` to `.env` and adjust the `VITE_*_URL` values.

## Notes on the backend as found

- `GET /cart/getcart` and `DELETE /cart/deleteallcart` read the customer id from an `id` header rather than the JWT (the other cart endpoints use `Authorization`). The frontend decodes the JWT client-side and sends both an `Authorization` header and an `id` header on every request so all cart endpoints work.
- Register expects `dateOfBirth` as `dd-MM-yyyy`; the frontend converts from the browser's native date input format automatically.
