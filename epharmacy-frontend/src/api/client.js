import axios from "axios";

// All requests go through the nginx reverse proxy at the SAME origin as
// the frontend itself (e.g. http://localhost:5173/api/...), which then
// forwards to the api-gateway container internally over the Docker
// network. This is deliberate:
//
//   - No CORS handling is needed in the browser at all, since the
//     request origin and the page origin are identical.
//   - The browser never needs to know the gateway's host port, so it
//     can't collide with another service (Jenkins, another container,
//     etc.) that happens to also be bound to 8080 on the host.
//   - The gateway's own host port mapping in docker-compose.yaml only
//     needs to exist for your own manual curl/debugging convenience —
//     the running app doesn't depend on it.
//
// VITE_GATEWAY_URL is still supported as an escape hatch (e.g. for
// hitting the gateway directly during local dev without Docker/nginx in
// the loop), but the default is now the proxied relative path.
export const GATEWAY_URL = import.meta.env.VITE_GATEWAY_URL || "/api";

const TOKEN_KEY = "epharmacy_token";

// When the JWT is missing/expired, every protected endpoint (cart, order,
// payment, profile, ...) comes back 401 from Spring Security's default
// entry point. Catch that once, here, instead of in every page.
function handleSessionExpired() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem("epharmacy_customer_id");
  localStorage.removeItem("epharmacy_customer_name");

  if (window.location.pathname !== "/login") {
    // Full reload (not react-router navigate) so AuthContext/CartContext
    // re-initialize from the now-empty localStorage instead of holding
    // stale in-memory auth state.
    window.location.href = `/login?sessionExpired=1&from=${encodeURIComponent(
      window.location.pathname
    )}`;
  }
}

function makeClient(baseURL) {
  const instance = axios.create({ baseURL, headers: { "Content-Type": "application/json" } });
  instance.interceptors.request.use((config) => {
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  });
  instance.interceptors.response.use(
    (response) => response,
    (error) => {
      const status = error?.response?.status;
      // 401 = no/expired/invalid token. Only treat 403 as expiry when we
      // actually had a token (a plain "not permitted" 403 shouldn't log
      // the user out).
      const hadToken = Boolean(localStorage.getItem(TOKEN_KEY));
      if (status === 401 || (status === 403 && hadToken)) {
        handleSessionExpired();
      }
      return Promise.reject(error);
    }
  );
  return instance;
}

// One shared axios instance for every service, pointed at the gateway
// (via the nginx /api proxy by default — see GATEWAY_URL above).
const gatewayApi = makeClient(GATEWAY_URL);

export const medicineApi = gatewayApi;
export const customerApi = gatewayApi;
export const cartApi = gatewayApi;
export const orderApi = gatewayApi;
export const paymentApi = gatewayApi;

export function extractErrorMessage(err, fallback) {
  const body = err?.response?.data;
  return (
    (typeof body?.data === "string" ? body.data : null) ||
    body?.message ||
    body?.error ||
    (typeof body === "string" ? body : null) ||
    err?.message ||
    fallback
  );
}

/**
 * Every endpoint this app calls, grouped by microservice — kept here as a
 * single index so the whole surface area of the backend is visible from
 * one place. All requests go to GATEWAY_URL; the path prefix below tells
 * the gateway which service to route each request to.
 *
 *  pharmacy-user-service      (mounted at /customer)
 *  pharmacy-medicine-service  (mounted at /medicine)
 *  pharmacy-cart-service      (mounted at /cart)
 *  pharmacy-order-service     (mounted at /order)
 *  pharmacy-payment-service   (mounted at /payment)
 */
export const endpoints = {
  customer: {
    register: (body) => customerApi.post("/customer/register", body),
    login: (body) => customerApi.post("/customer/login", body),
    profile: () => customerApi.get("/customer/profile"),
    updateProfile: (body) => customerApi.put("/customer/update-profile", body),
    changePassword: (body) => customerApi.put("/customer/change-password", body),
    viewAddress: () => customerApi.get("/customer/view-address"),
    addAddress: (body) => customerApi.post("/customer/add-address", body),
    getAddress: (addressId) => customerApi.get(`/customer/getaddress/${addressId}`),
  },
  medicine: {
    addMedicine: (body) => medicineApi.post("/medicine/add-medicine", body),
    getAll: (page, size = 8) => medicineApi.get(`/medicine/get-all/${page}`, { params: { size } }),
    getByCategory: (category, page, size = 8) =>
      medicineApi.get(`/medicine/get-category/${encodeURIComponent(category)}/${page}`, { params: { size } }),
    getById: (id) => medicineApi.get(`/medicine/getbyid/${id}`),
    updateStock: (id, orderedQuantity) => medicineApi.put(`/medicine/update-stock/${id}`, orderedQuantity),
    search: (name, page, size = 8) =>
      medicineApi.get(`/medicine/serach/${encodeURIComponent(name)}/${page}`, { params: { size } }),
  },
  cart: {
    add: (medicineId, body) => cartApi.post(`/cart/addcart/${medicineId}`, body),
    get: () => cartApi.get("/cart/getcart"),
    update: (medicineId, body) => cartApi.put(`/cart/updatecart/${medicineId}`, body),
    remove: (medicineId) => cartApi.delete(`/cart/deletecart/${medicineId}`),
    clear: () => cartApi.delete("/cart/deleteallcart"),
  },
  order: {
    place: (body) => orderApi.post("/order/place-order", body),
    listForCustomer: (customerId) => orderApi.get(`/order/view-order/customer/${customerId}`),
    cancel: (orderId, body) => orderApi.put(`/order/cancel-order/${orderId}`, body),
    confirmPayment: (orderId, paymentId) =>
      orderApi.put(`/order/${orderId}/payment-success`, null, { params: { paymentId } }),
    track: (orderId) => orderApi.get(`/order/getorderid/${orderId}`),
  },
  payment: {
    pay: (body) => paymentApi.post("/payment/pay", body),
    addCard: (body) => paymentApi.post("/payment/card/addcard", body),
    getCards: () => paymentApi.get("/payment/getcards"),
  },
};