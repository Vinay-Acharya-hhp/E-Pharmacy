import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { endpoints, extractErrorMessage } from "../api/client";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import Icon from "../components/Icon";
import "./Orders.css";

const STATUS_PILL = {
  PROCESSING: "pill--processing",
  CONFIRMED: "pill--confirmed",
  COMPLETED: "pill--completed",
  CANCELLED: "pill--cancelled",
};

function PayNowForm({ order, onPaid }) {
  const [cards, setCards] = useState([]);
  const [cardId, setCardId] = useState(null);
  const [cvv, setCvv] = useState("");
  const [loading, setLoading] = useState(true);
  const [paying, setPaying] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    endpoints.payment
      .getCards()
      .then((res) => {
        const list = res.data?.data || [];
        setCards(list);
        if (list[0]) setCardId(list[0].cardId);
      })
      .catch(() => setCards([]))
      .finally(() => setLoading(false));
  }, []);

  async function handlePay(e) {
    e.preventDefault();
    setError(null);
    setPaying(true);
    try {
      await endpoints.payment.pay({ orderId: order.orderId, cardId, cvv });
      try {
        await endpoints.order.confirmPayment(order.orderId, Date.now());
      } catch {
        // payment already succeeded; confirmation is best-effort, see Checkout.jsx
      }
      onPaid();
    } catch (err) {
      setError(extractErrorMessage(err, "Payment failed."));
    } finally {
      setPaying(false);
    }
  }

  if (loading) return <p className="field-hint">Loading your cards…</p>;

  if (cards.length === 0) {
    return (
      <div className="hint-banner">
        No saved card yet.{" "}
        <Link to="/cards" className="link-plain">Add one</Link> to pay for this order.
      </div>
    );
  }

  return (
    <form className="inline-form pay-now" onSubmit={handlePay}>
      {error && <div className="error-banner">{error}</div>}
      <div className="field-row">
        <div className="field">
          <label>Card</label>
          <select value={cardId || ""} onChange={(e) => setCardId(e.target.value)}>
            {cards.map((c) => (
              <option key={c.cardId} value={c.cardId}>
                {c.nameOnCard} · ending {c.cardId.slice(-4)}
              </option>
            ))}
          </select>
        </div>
        <div className="field">
          <label>CVV</label>
          <input required maxLength={3} value={cvv} onChange={(e) => setCvv(e.target.value)} />
        </div>
      </div>
      <button className="btn btn-primary" disabled={paying}>
        {paying ? "Processing…" : `Pay ₹${Number(order.amountPaid || 10).toFixed(2)}`}
      </button>
    </form>
  );
}

export default function Orders() {
  const { isAuthenticated, customerId } = useAuth();
  const { refresh: refreshCart } = useCart();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [actionId, setActionId] = useState(null);
  const [payingId, setPayingId] = useState(null);
  const [cancellingId, setCancellingId] = useState(null);
  const [cancelReason, setCancelReason] = useState("Changed my mind");

  async function loadOrders() {
    setLoading(true);
    setError(null);
    try {
      const res = await endpoints.order.listForCustomer(customerId);
      setOrders(res.data?.data || []);
    } catch (err) {
      setError(extractErrorMessage(err, "Could not load orders."));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (!isAuthenticated || !customerId) {
      setLoading(false);
      return;
    }
    loadOrders();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated, customerId]);

  async function refreshStatus(orderId) {
    setActionId(orderId);
    try {
      const res = await endpoints.order.track(orderId);
      const fresh = res.data?.data;
      setOrders((current) =>
        current.map((o) => (o.orderId === orderId ? { ...o, orderStatus: fresh.orderStatus } : o))
      );
    } catch (err) {
      setError(extractErrorMessage(err, "Could not refresh this order's status."));
    } finally {
      setActionId(null);
    }
  }

  async function confirmCancel(orderId) {
    setActionId(orderId);
    setError(null);
    try {
      const res = await endpoints.order.cancel(orderId, { cancelReason });
      const updated = res.data?.data;
      setOrders((current) => current.map((o) => (o.orderId === orderId ? updated : o)));
      setCancellingId(null);
    } catch (err) {
      setError(extractErrorMessage(err, "Could not cancel this order."));
    } finally {
      setActionId(null);
    }
  }

  if (!isAuthenticated) {
    return (
      <div className="container page">
        <div className="empty-state">
          <div className="icon-badge"><Icon name="box" size={22} /></div>
          <h2>Sign in to view orders</h2>
          <p>Your order history is linked to your account.</p>
          <Link to="/login" className="btn btn-primary">Sign in</Link>
        </div>
      </div>
    );
  }

  return (
    <main className="container page orders">
      <div className="page__header">
        <div>
          <p className="page__eyebrow">Orders · pharmacy-order-service</p>
          <h1 className="page__title">Order history</h1>
        </div>
        <Link to="/" className="btn btn-ghost">Continue shopping</Link>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {loading ? (
        <p className="orders__status">Loading your orders…</p>
      ) : orders.length === 0 ? (
        <div className="empty-state">
          <h2>No orders yet</h2>
          <p>Placed orders will appear here with payment and delivery status.</p>
          <Link to="/" className="btn btn-primary">Browse catalog</Link>
        </div>
      ) : (
        <div className="orders__list">
          {orders.map((order) => (
            <article className="order-card" key={order.orderId}>
              <div className="order-card__top">
                <div>
                  <p className="order-card__eyebrow">Order #{order.orderId}</p>
                  <span className={`pill ${STATUS_PILL[order.orderStatus] || "pill--neutral"}`}>
                    {order.orderStatus}
                  </span>
                </div>
                <span className="order-card__amount">₹{Number(order.amountPaid || 0).toFixed(2)}</span>
              </div>

              <div className="order-card__meta">
                <span>Delivery: {order.deliveryStatus?.replaceAll("_", " ") || "—"}</span>
                <span>Expected: {order.expectedDeliveryDate || "Pending"}</span>
              </div>

              <div className="order-items">
                {(order.orderItems || []).map((item) => (
                  <div className="order-item" key={item.orderItemId || item.medicineId}>
                    <Link to={`/medicine/${item.medicineId}`} className="link-plain">
                      {item.medicineName}
                    </Link>
                    <span>
                      {item.quantity} × ₹{Number(item.price || 0).toFixed(2)}
                    </span>
                  </div>
                ))}
              </div>

              <div className="inline-actions">
                <button
                  className="btn btn-ghost"
                  disabled={actionId === order.orderId}
                  onClick={() => refreshStatus(order.orderId)}
                >
                  <Icon name="truck" size={14} /> Refresh status
                </button>

                {order.orderStatus === "PROCESSING" && (
                  <>
                    <button
                      className="btn btn-primary"
                      onClick={() => setPayingId(payingId === order.orderId ? null : order.orderId)}
                    >
                      {payingId === order.orderId ? "Cancel payment" : "Pay now"}
                    </button>
                    <button
                      className="btn btn-ghost"
                      onClick={() => setCancellingId(cancellingId === order.orderId ? null : order.orderId)}
                    >
                      Cancel order
                    </button>
                  </>
                )}
              </div>

              {payingId === order.orderId && (
                <PayNowForm
                  order={order}
                  onPaid={async () => {
                    setPayingId(null);
                    await loadOrders();
                    await refreshCart();
                  }}
                />
              )}

              {cancellingId === order.orderId && (
                <form
                  className="inline-form"
                  onSubmit={(e) => {
                    e.preventDefault();
                    confirmCancel(order.orderId);
                  }}
                >
                  <div className="field">
                    <label>Reason for cancelling</label>
                    <input
                      required
                      value={cancelReason}
                      onChange={(e) => setCancelReason(e.target.value)}
                    />
                  </div>
                  <div className="inline-form__actions">
                    <button className="btn btn-primary" disabled={actionId === order.orderId}>
                      {actionId === order.orderId ? "Cancelling…" : "Confirm cancellation"}
                    </button>
                    <button type="button" className="btn btn-ghost" onClick={() => setCancellingId(null)}>
                      Keep order
                    </button>
                  </div>
                </form>
              )}
            </article>
          ))}
        </div>
      )}
    </main>
  );
}
