import axios from "axios";

// Each backend microservice is called on its own port. The gateway
// (port 8080) has no CORS configuration of its own, but every individual
// service already allows http://localhost:5173, so the browser talks to
// each service directly — same approach the backend was set up for.
const PORTS = {
  medicine: import.meta.env.VITE_MEDICINE_URL || "http://localhost:8082",
  customer: import.meta.env.VITE_CUSTOMER_URL || "http://localhost:8081",
  cart: import.meta.env.VITE_CART_URL || "http://localhost:8083",
  order: import.meta.env.VITE_ORDER_URL || "http://localhost:8084",
  payment: import.meta.env.VITE_PAYMENT_URL || "http://localhost:8085",
};

function makeClient(baseURL) {
  const instance = axios.create({ baseURL });
  instance.interceptors.request.use((config) => {
    const token = localStorage.getItem("epharmacy_token");
    const customerId = localStorage.getItem("epharmacy_customer_id");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    if (customerId) {
      config.headers.id = customerId;
    }
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
