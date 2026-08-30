import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { endpoints, extractErrorMessage } from "../api/client";
import { useAuth } from "../context/AuthContext";
import Icon from "../components/Icon";
import "./Cards.css";

const emptyCard = { cardId: "", nameOnCard: "", cardType: "CREDIT", cvv: "", expiryDate: "", balance: "" };

export default function Cards() {
  const { isAuthenticated } = useAuth();
  const [cards, setCards] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyCard);
  const [saving, setSaving] = useState(false);

  async function load() {
    setLoading(true);
    setError(null);
    try {
      const res = await endpoints.payment.getCards();
      setCards(res.data?.data || []);
    } catch (err) {
      // A brand-new account with no cards gets a 404 from the backend —
      // that's an empty state here, not a real error.
      setCards([]);
      const msg = extractErrorMessage(err, "");
      if (err?.response?.status && err.response.status !== 404) setError(msg);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (isAuthenticated) load();
    else setLoading(false);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated]);

  async function handleSave(e) {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      await endpoints.payment.addCard({ ...form, balance: Number(form.balance) || 0 });
      setForm(emptyCard);
      setShowForm(false);
      await load();
    } catch (err) {
      setError(extractErrorMessage(err, "Could not save this card."));
    } finally {
      setSaving(false);
    }
  }

  if (!isAuthenticated) {
    return (
      <div className="container page">
        <div className="empty-state">
          <div className="icon-badge"><Icon name="card" size={22} /></div>
          <h2>Sign in to manage payment methods</h2>
          <p>Saved cards are used to pay for orders at checkout.</p>
          <Link to="/login" className="btn btn-primary">Sign in</Link>
        </div>
      </div>
    );
  }

  return (
    <main className="container page cards-page">
      <div className="page__header">
        <div>
          <p className="page__eyebrow">Account · pharmacy-payment-service</p>
          <h1 className="page__title">Payment methods</h1>
        </div>
        <button className="btn btn-primary" onClick={() => setShowForm((v) => !v)}>
          <Icon name="plus" size={15} /> {showForm ? "Close" : "Add card"}
        </button>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {showForm && (
        <form className="panel" onSubmit={handleSave}>
          <h2 className="panel__title">New card</h2>
          <p className="panel__hint">
            This is a demo payment rail — cards are stored and really debited by the payment
            service, so give it a starting balance.
          </p>
          <div className="field-row">
            <div className="field">
              <label>Card number</label>
              <input
                required
                value={form.cardId}
                onChange={(e) => setForm((f) => ({ ...f, cardId: e.target.value }))}
                placeholder="4111111111111111"
              />
            </div>
            <div className="field">
              <label>Name on card</label>
              <input
                required
                value={form.nameOnCard}
                onChange={(e) => setForm((f) => ({ ...f, nameOnCard: e.target.value }))}
              />
            </div>
          </div>
          <div className="field-row">
            <div className="field">
              <label>Type</label>
              <select value={form.cardType} onChange={(e) => setForm((f) => ({ ...f, cardType: e.target.value }))}>
                <option value="CREDIT">Credit</option>
                <option value="DEBIT">Debit</option>
              </select>
            </div>
            <div className="field">
              <label>CVV</label>
              <input
                required
                maxLength={3}
                value={form.cvv}
                onChange={(e) => setForm((f) => ({ ...f, cvv: e.target.value }))}
              />
            </div>
            <div className="field">
              <label>Expiry</label>
              <input
                required
                type="date"
                value={form.expiryDate}
                onChange={(e) => setForm((f) => ({ ...f, expiryDate: e.target.value }))}
              />
            </div>
          </div>
          <div className="field">
            <label>Starting balance</label>
            <input
              required
              type="number"
              min="0"
              step="0.01"
              value={form.balance}
              onChange={(e) => setForm((f) => ({ ...f, balance: e.target.value }))}
              placeholder="5000"
            />
          </div>
          <button className="btn btn-primary" disabled={saving}>
            {saving ? "Saving…" : "Save card"}
          </button>
        </form>
      )}

      {loading ? (
        <p className="muted">Loading your cards…</p>
      ) : cards.length === 0 ? (
        <div className="empty-state">
          <h2>No payment methods yet</h2>
          <p>Add a card so you can check out without re-entering details.</p>
        </div>
      ) : (
        <div className="stack-list">
          {cards.map((c) => (
            <div className="list-row" key={c.cardId}>
              <div className="list-row__main">
                <span className="list-row__icon"><Icon name="card" size={17} /></span>
                <span>
                  <p className="list-row__title">
                    {c.nameOnCard} · ending {c.cardId.slice(-4)}
                  </p>
                  <p className="list-row__sub">
                    {c.cardType} · expires {c.expiryDate} · balance ₹{Number(c.balance || 0).toFixed(2)}
                  </p>
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </main>
  );
}
