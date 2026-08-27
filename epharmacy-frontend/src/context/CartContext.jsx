import { createContext, useCallback, useContext, useEffect, useState } from "react";
import { cartApi, extractErrorMessage } from "../api/client";
import { useAuth } from "./AuthContext";

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const { isAuthenticated, customerId } = useAuth();
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const refresh = useCallback(async () => {
    if (!isAuthenticated) {
      setItems([]);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const res = await cartApi.get("/cart/getcart");
      setItems(res.data?.data || []);
    } catch (err) {
      setError(extractErrorMessage(err, "Could not load your cart"));
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated, customerId]);

  useEffect(() => {
    refresh();
  }, [refresh]);

  async function addToCart(medicineId, quantity = 1) {
    await cartApi.post(`/cart/addcart/${medicineId}`, { quantity });
    await refresh();
  }

  async function updateQuantity(medicineId, quantity) {
    await cartApi.put(`/cart/updatecart/${medicineId}`, { quantity });
    await refresh();
  }

  async function removeFromCart(medicineId) {
    await cartApi.delete(`/cart/deletecart/${medicineId}`);
    await refresh();
  }

  async function clearCart() {
    await cartApi.delete("/cart/deleteallcart");
    await refresh();
  }

  const count = items.reduce((sum, item) => sum + (item.quantity || 1), 0);

  const value = {
    items,
    loading,
    error,
    count,
    refresh,
    addToCart,
    updateQuantity,
    removeFromCart,
    clearCart,
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const ctx = useContext(CartContext);
  if (!ctx) throw new Error("useCart must be used within CartProvider");
  return ctx;
}
