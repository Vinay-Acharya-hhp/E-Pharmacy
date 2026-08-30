import axios from "axios";

// Every backend microservice is called on its own port. The API gateway
// (port 8080) has no CORS configuration of its own, but each individual
// service already allow-lists http://localhost:5173, so the browser talks
// to each service directly — the same approach the backend ships with.
export const PORTS = {
  medicine: import.meta.env.VITE_MEDICINE_URL || "http://localhost:8082",
  customer: import.meta.env.VITE_CUSTOMER_URL || "http://localhost:8081",
  cart: import.meta.env.VITE_CART_URL || "http://localhost:8083",
  order: import.meta.env.VITE_ORDER_URL || "http://localhost:8084",
  payment: import.meta.env.VITE_PAYMENT_URL || "http://localhost:8085",
};

const TOKEN_KEY = "epharmacy_token";

function makeClient(baseURL) {
  const instance = axios.create({ baseURL, headers: { "Content-Type": "application/json" } });
  instance.interceptors.request.use((config) => {
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  });
  return instance;
}

export const medicineApi = makeClient(PORTS.medicine);
export const customerApi = makeClient(PORTS.customer);
export const cartApi = makeClient(PORTS.cart);
export const orderApi = makeClient(PORTS.order);
export const paymentApi = makeClient(PORTS.payment);

export function extractErrorMessage(err, fallback) {
  return (
    err?.response?.data?.message ||
    err?.response?.data?.error ||
    (typeof err?.response?.data === "string" ? err.response.data : null) ||
    err?.message ||
    fallback
  );
}

/**
 * Every endpoint this app calls, grouped by microservice — kept here as a
 * single index so the whole surface area of the backend is visible from
 * one place.
 *
 *  pharmacy-user-service      (8081, mounted at /customer)
 *  pharmacy-medicine-service  (8082, mounted at /medicine)
 *  pharmacy-cart-service      (8083, mounted at /cart)
 *  pharmacy-order-service     (8084, mounted at /order)
 *  pharmacy-payment-service   (8085, mounted at /payment)
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
