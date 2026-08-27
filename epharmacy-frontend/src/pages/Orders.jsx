import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { orderApi, extractErrorMessage } from "../api/client";
import { useAuth } from "../context/AuthContext";
import "./Orders.css";

export default function Orders() {
  const { isAuthenticated, customerId } = useAuth();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [actionId, setActionId] = useState(null);

  useEffect(() => {
    if (!isAuthenticated || !customerId) {
      setLoading(false);
      return;
    }

    async function loadOrders() {
      setLoading(true);
      setError(null);
      try {
        const res = await orderApi.get(`/order/view-order/customer/${customerId}`);
        setOrders(res.data?.data || []);
      } catch (err) {
        setError(extractErrorMessage(err, "Could not load orders."));
      } finally {
        setLoading(false);
      }
    }

    loadOrders();
  }, [isAuthenticated, customerId]);

  async function cancelOrder(orderId) {
    setActionId(orderId);
    setError(null);
    try {
      const res = await orderApi.put(`/order/cancel-order/${orderId}`, {
        reason: "Cancelled by customer",
      });
      const updated = res.data?.data;
      setOrders((current) =>
        current.map((order) => (order.orderId === orderId ? updated : order))
      );
    } catch (err) {
      setError(extractErrorMessage(err, "Could not cancel this order."));
    } finally {
      setActionId(null);
    }
  }

  if (!isAuthenticated) {
    return (
      <div className="container orders-empty">
        <h1>Sign in to view orders</h1>
        <p>Your order history is linked to your account.</p>
        <Link to="/login" className="btn btn-primary">
          Sign in
        </Link>
      </div>
    );
  }

  return (
    <main className="container orders">
      <div className="orders__header">
        <h1>Orders</h1>
        <Link to="/" className="btn btn-ghost">
          Continue shopping
        </Link>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {loading ? (
        <p className="orders__status">Loading your orders...</p>
      ) : orders.length === 0 ? (
        <div className="orders-empty">
          <h2>No orders yet</h2>
          <p>Placed orders will appear here with payment and delivery status.</p>
          <Link to="/" className="btn btn-primary">
            Browse catalog
          </Link>
        </div>
      ) : (
        <div className="orders__list">
          {orders.map((order) => (
            <article className="order-card" key={order.orderId}>
              <div className="order-card__top">
                <div>
                  <p className="order-card__eyebrow">Order #{order.orderId}</p>
                  <h2>{order.orderStatus}</h2>
                </div>
                <span className="order-card__amount">
                  Rs. {Number(order.amountPaid || 0).toFixed(2)}
                </span>
              </div>

              <div className="order-card__meta">
                <span>Delivery: {order.deliveryStatus}</span>
                <span>Expected: {order.expectedDeliveryDate || "Pending"}</span>
              </div>

              <div className="order-items">
                {(order.orderItems || []).map((item) => (
                  <div className="order-item" key={item.orderItemId || item.medicineId}>
                    <span>{item.medicineName}</span>
                    <span>
                      {item.quantity} x Rs. {Number(item.price || 0).toFixed(2)}
                    </span>
                  </div>
                ))}
              </div>

              {order.orderStatus === "PROCESSING" && (
                <button
                  className="btn btn-ghost"
                  disabled={actionId === order.orderId}
                  onClick={() => cancelOrder(order.orderId)}
                >
                  {actionId === order.orderId ? "Cancelling..." : "Cancel order"}
                </button>
              )}
            </article>
          ))}
        </div>
      )}
    </main>
  );
}
