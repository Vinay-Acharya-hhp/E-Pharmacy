# E-Pharmacy — Frontend

A React (Vite) storefront for the E-Pharmacy Spring Boot microservices backend
(Eureka + gateway + user, medicine, cart, order and payment services).

## Pages → endpoints

Every REST endpoint exposed by the five services is wired into the UI somewhere.
`src/api/client.js` keeps a single indexed map (`endpoints.*`) of all of them.

| Page | Microservice | Endpoints used |
|---|---|---|
| Catalog (`/`) | medicine | `GET /medicine/get-all/{page}`, `GET /medicine/get-category/{category}/{page}`, `GET /medicine/serach/{name}/{page}` |
| Medicine detail (`/medicine/:id`) | medicine, cart | `GET /medicine/getbyid/{id}`, `POST /cart/addcart/{id}` |
| Cart (`/cart`) | cart, medicine | `GET /cart/getcart`, `PUT /cart/updatecart/{id}`, `DELETE /cart/deletecart/{id}`, `DELETE /cart/deleteallcart`, `GET /medicine/getbyid/{id}` |
| Checkout (`/checkout`) | customer, payment, order | `GET/POST /customer/view-address` `/add-address`, `GET/POST /payment/getcards` `/card/addcard`, `POST /order/place-order`, `POST /payment/pay`, `PUT /order/{id}/payment-success` |
| Orders (`/orders`) | order, payment | `GET /order/view-order/customer/{id}`, `GET /order/getorderid/{id}`, `PUT /order/cancel-order/{id}`, `POST /payment/pay`, `PUT /order/{id}/payment-success` |
| Track order (`/track-order`) | order | `GET /order/getorderid/{id}` — public, no sign-in |
| Profile (`/profile`) | customer | `GET /customer/profile`, `PUT /customer/update-profile`, `PUT /customer/change-password` |
| Addresses (`/addresses`) | customer | `GET /customer/view-address`, `POST /customer/add-address`, `GET /customer/getaddress/{id}` |
| Payment methods (`/cards`) | payment | `GET /payment/getcards`, `POST /payment/card/addcard` |
| Pharmacist console (`/admin`) | medicine | `POST /medicine/add-medicine`, `PUT /medicine/update-stock/{id}` |
| Register / Login | customer | `POST /customer/register`, `POST /customer/login` |

## Photos

Medicine photos are real static files, not remote URLs or generated art:

- `public/images/catalog/` bundles the 9 usable images that shipped in
  `pharmacy_medicine_service/src/main/resources/static/images/` (the other
  4 files in that folder are mis-saved `.htm` pages, not images, so they're
  skipped).
- If a medicine's `imageUrl` is a full `http(s)` URL, it's used as-is.
- If it's a relative path under `/images/catalog/...`, it's one of this
  app's bundled photos.
- Any other relative path (e.g. one pointing at the medicine service's own
  `static/images` folder) is resolved against the medicine service's own
  host, so real backend-hosted photos work too.
- If a medicine has no photo at all, `src/utils/medicineImages.js` picks a
  deterministic static photo by category (see `CATEGORY_IMAGE`) — never a
  drawn/generated placeholder.

The pharmacist console (`/admin`) lets you attach one of the bundled photos
to a new listing from a dropdown, or leave it blank to fall back to the
category default.

## Bugs fixed vs. the original scaffold

While wiring up every endpoint against the actual controller code, a few
mismatches turned up between this frontend and the real backend:

- **Payment.** The checkout flow called `POST /payment/amount/{amount}`,
  which is commented out in `PaymentController.java`. It now calls the real
  endpoint, `POST /payment/pay`, with `{ orderId, cardId, cvv }`.
- **Card creation.** The card form sent `cartType`; the DTO field is
  `cardType`. It also never sent `balance`, which the payment service
  requires to actually debit the card. Both are fixed, and the form now
  asks for a starting balance.
- **Cancel order.** The cancel call sent `{ reason }`; `CancelOrderRequestDto`
  expects `cancelReason`.

## A known backend gap (documented, not silently patched)

`PUT /order/{orderId}/payment-success` expects a numeric `paymentId`
(it's stored on the `Order` entity as a `Long`), but
`pharmacy-payment-service` generates a string transaction id
(`"TXN-<uuid>"`) and the Feign call that was meant to forward it to the
order service is commented out in `PaymentServiceImp.java`. So there's no
real numeric id to send.

This frontend calls `payment-success` right after a successful payment
using a timestamp as a stand-in id, purely so the order flips to
`CONFIRMED`, stock gets decremented, and the cart gets cleared
server-side. The payment itself (card debit, payment record) already
succeeded independently by that point — this call only affects order
bookkeeping, not what was charged. If you want the two services properly
linked, that's a one-line backend fix (uncomment the Feign call in
`PaymentServiceImp.payForOrder` and have it pass a real numeric id).

## Running it

1. Start the backend: Eureka, the gateway, and all five services, with a
   running MySQL instance.
2. In this folder:
   ```bash
   npm install
   npm run dev
   ```
3. Open http://localhost:5173

The frontend calls each microservice **directly on its own port**
(8081–8085), not through the gateway — the gateway has no CORS
configuration of its own, but every service individually allow-lists
`http://localhost:5173`. If your ports differ, copy `.env.example` to
`.env` and adjust the `VITE_*_URL` values.

## Notes on the backend as found

- Register expects `dateOfBirth` as `dd-MM-yyyy`; the pharmacist console's
  medicine dates use the same format. Both forms convert automatically from
  the browser's native `yyyy-mm-dd` date input.
- `/medicine/**`, `/order/getorderid/**`, `/customer/register`, and
  `/customer/login` are the only routes that don't require a bearer token;
  everything else does.
- `PUT /medicine/update-stock/{id}` *decrements* stock by the amount you
  send — it's the same call the order service makes internally once a
  payment is confirmed. The pharmacist console exposes it for manual
  corrections.
