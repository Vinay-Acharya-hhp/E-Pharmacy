import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { endpoints, extractErrorMessage } from "../api/client";
import Icon from "../components/Icon";
import "./TrackOrder.css";

const STATUS_PILL = {
  PROCESSING: "pill--processing",
  CONFIRMED: "pill--confirmed",
  COMPLETED: "pill--completed",
  CANCELLED: "pill--cancelled",
};

export default function TrackOrder() {
  const { orderId: paramOrderId } = useParams();
  const navigate = useNavigate();
  const [orderId, setOrderId] = useState(paramOrderId || "");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searched, setSearched] = useState(false);

  async function handleSearch(e) {
    e?.preventDefault();
    if (!orderId) return;
    navigate(`/track-order/${orderId}`, { replace: true });
    setLoading(true);
    setError(null);
    setSearched(true);
    try {
      const res = await endpoints.order.track(orderId);
      setResult(res.data?.data);
    } catch (err) {
      setResult(null);
      setError(extractErrorMessage(err, "No order found with that ID."));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="container page track-order">
      <p className="page__eyebrow">Track order · pharmacy-order-service</p>
      <h1 className="page__title">Where's my order?</h1>
      <p className="track-order__sub">
        Look up live status and amount for any order — no sign-in required.
      </p>

      <form className="track-order__form" onSubmit={handleSearch}>
        <div className="field mt-0">
          <label htmlFor="order-id">Order ID</label>
          <input
            id="order-id"
            required
            inputMode="numeric"
            value={orderId}
            onChange={(e) => setOrderId(e.target.value)}
            placeholder="e.g. 104"
          />
        </div>
        <button className="btn btn-primary" disabled={loading}>
          <Icon name="search" size={15} /> {loading ? "Looking up…" : "Track"}
        </button>
      </form>

      {error && <div className="error-banner">{error}</div>}

      {result && (
        <div className="panel track-order__result">
          <div className="track-order__result-top">
            <div>
              <p className="page__eyebrow">Order #{result.orderId}</p>
              <span className={`pill ${STATUS_PILL[result.orderStatus] || "pill--neutral"}`}>
                {result.orderStatus}
              </span>
            </div>
            <span className="order-card__amount">₹{Number(result.amount || 0).toFixed(2)}</span>
          </div>
          <dl className="track-order__facts">
            <div><dt>Customer ID</dt><dd>#{result.customerId}</dd></div>
            <div><dt>Amount due</dt><dd>₹{Number(result.amount || 0).toFixed(2)}</dd></div>
          </dl>
        </div>
      )}

      {searched && !loading && !result && !error && (
        <div className="empty-state">
          <h3>Nothing to show</h3>
        </div>
      )}
    </div>
  );
}
