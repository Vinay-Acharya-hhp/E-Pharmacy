import { createContext, useContext, useEffect, useState } from "react";
import { customerApi, extractErrorMessage } from "../api/client";
import { decodeJwt } from "../utils/jwt";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("epharmacy_token"));
  const [customerId, setCustomerId] = useState(() => localStorage.getItem("epharmacy_customer_id"));
  const [customerName, setCustomerName] = useState(() => localStorage.getItem("epharmacy_customer_name"));

  useEffect(() => {
    if (token) localStorage.setItem("epharmacy_token", token);
    else localStorage.removeItem("epharmacy_token");
  }, [token]);

  useEffect(() => {
    if (customerId) localStorage.setItem("epharmacy_customer_id", customerId);
    else localStorage.removeItem("epharmacy_customer_id");
  }, [customerId]);

  async function login(email, password) {
    const res = await customerApi.post("/customer/login", {
      customerEmailId: email,
      password,
    });
    const receivedToken = res.data;
    const claims = decodeJwt(receivedToken);
    setToken(receivedToken);
    setCustomerId(claims?.id ? String(claims.id) : null);
    const name = claims?.sub || email;
    setCustomerName(name);
    localStorage.setItem("epharmacy_customer_name", name);
    return receivedToken;
  }

  async function register(payload) {
    try {
      const res = await customerApi.post("/customer/register", payload);
      return res.data;
    } catch (err) {
      throw new Error(extractErrorMessage(err, "Registration failed"));
    }
  }

  function logout() {
    setToken(null);
    setCustomerId(null);
    setCustomerName(null);
    localStorage.removeItem("epharmacy_customer_name");
  }

  const value = {
    token,
    customerId,
    customerName,
    isAuthenticated: Boolean(token),
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
