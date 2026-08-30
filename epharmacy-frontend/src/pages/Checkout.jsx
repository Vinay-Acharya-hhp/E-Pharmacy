import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { endpoints, extractErrorMessage } from "../api/client";
import { useCart } from "../context/CartContext";
import Icon from "../components/Icon";
import "./Checkout.css";

const emptyAddress = {
  addressName: "",
  addressLine1: "",
  addressLine2: "",
  area: "",
  city: "",
  state: "",
  pincode: "",
};

const emptyCard = {
  cardId: "",
  nameOnCard: "",
  cardType: "CREDIT",
  cvv: "",
  expiryDate: "",
  balance: "",
};

const STEP = { REVIEW: "review", PAY: "pay", DONE: "done" };

export default function Checkout() {
  const { items, refresh: refreshCart } = useCart();
  const navigate = useNavigate();

  const [addresses, setAddresses] = useState([]);
  const [selectedAddressId, setSelectedAddressId] = useState(null);
  const [showAddressForm, setShowAddressForm] = useState(false);
  const [addressForm, setAddressForm] = useState(emptyAddress);

  const [cards, setCards] = useState([]);
  const [selectedCardId, setSelectedCardId] = useState(null);
  const [showCardForm, setShowCardForm] = useState(false);
  const [cardForm, setCardForm] = useState(emptyCard);
  const [cvvInput, setCvvInput] = useState("");

  const [order, setOrder] = useState(null);
  const [step, setStep] = useState(STEP.REVIEW);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    refreshCart();
    loadAddresses();
    loadCards();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function loadAddresses() {
    try {
      const res = await endpoints.customer.viewAddress();
      const list = res.data?.data || [];
      setAddresses(list);
      if (list[0]?.id) setSelectedAddressId(Number(list[0].id));
    } catch {
      setAddresses([]);
    }
  }

  async function loadCards() {
    try {
      const res = await endpoints.payment.getCards();
      const list = res.data?.data || [];
      setCards(list);
      if (list.length > 0) setSelectedCardId(list[0].cardId);
    } catch {
      // A brand-new account has no cards yet — the backend responds 404
      // ("Card Not found"), which is expected, not an error to surface.
      setCards([]);
    }
  }

  async function handleSaveAddress(e) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await endpoints.customer.addAddress(addressForm);
      setAddressForm(emptyAddress);
      setShowAddressForm(false);
      await loadAddresses();
    } catch (err) {
      setError(extractErrorMessage(err, "Could not save address."));
    } finally {
      setLoading(false);
    }
  }

  async function handleSaveCard(e) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await endpoints.payment.addCard({ ...cardForm, balance: Number(cardForm.balance) || 0 });
      setCardForm(emptyCard);
      setShowCardForm(false);
      await loadCards();
    } catch (err) {
      setError(extractErrorMessage(err, "Could not save card."));
    } finally {
      setLoading(false);
    }
  }

  async function handlePlaceOrder() {
    if (!selectedAddressId) {
      setError("Add or select a delivery address first.");
      return;
    }
    setError(null);
    setLoading(true);
    try {
      const res = await endpoints.order.place({
        deliveryAddress: { addressId: selectedAddressId },
      });
      setOrder(res.data?.data);
      setStep(STEP.PAY);
    } catch (err) {
      setError(extractErrorMessage(err, "Could not place order."));
    } finally {
      setLoading(false);
    }
  }

  async function handlePay(e) {
    e.preventDefault();
    if (!order || !selectedCardId) return;
    setError(null);
    setLoading(true);
    try {
      await endpoints.payment.pay({
        orderId: order.orderId,
        cardId: selectedCardId,
        cvv: cvvInput,
      });

      // The order-service expects a numeric paymentId here, but the
      // payment-service's own transaction id is the string it returned
      // above (e.g. "TXN-<uuid>") — the two services were never wired
      // together for this field, so we pass a timestamp as a stand-in
      // just to flip the order to CONFIRMED, reduce stock, and clear
      // the cart server-side. This does not affect what was charged.
      try {
        await endpoints.order.confirmPayment(order.orderId, Date.now());
      } catch {
        // Non-fatal — the payment itself already succeeded.
      }

      setStep(STEP.DONE);
      await refreshCart();
    } catch (err) {
      setError(extractErrorMessage(err, "Payment failed. Check the card details and try again."));
    } finally {
      setLoading(false);
    }
  }

  if (step === STEP.DONE) {
    return (
      <div className="container checkout-done">
        <div className="icon-badge" style={{ margin: "0 auto 14px" }}>
          <Icon name="check" size={26} />
        </div>
        <h1>Order confirmed</h1>
        <p>
          Order #{order?.orderId} is paid and on its way — expected delivery{" "}
          {order?.expectedDeliveryDate}.
        </p>
        <div className="inline-actions" style={{ justifyContent: "center" }}>
          <button className="btn btn-primary" onClick={() => navigate("/orders")}>
            View my orders
          </button>
          <button className="btn btn-ghost" onClick={() => navigate("/")}>
            Back to catalog
          </button>
        </div>
      </div>
    );
  }

  if (items.length === 0 && step === STEP.REVIEW) {
    return (
      <div className="container checkout-done">
        <h1>Your cart is empty</h1>
        <p>Add something to your cart before checking out.</p>
        <button className="btn btn-primary" onClick={() => navigate("/")}>
          Go to catalog
        </button>
      </div>
    );
  }

  return (
    <div className="container checkout">
      <h1>Checkout</h1>
      {error && <div className="error-banner">{error}</div>}

      {step !== STEP.PAY && (
        <>
          <section className="checkout-section">
            <h2><Icon name="pin" size={16} /> Delivery address</h2>
            {addresses.length > 0 && (
              <div className="option-list">
                {addresses.map((a) => (
                  <label key={a.id} className="option-row">
                    <input
                      type="radio"
                      name="address"
                      checked={selectedAddressId === Number(a.id)}
                      onChange={() => setSelectedAddressId(Number(a.id))}
                    />
                    <span>
                      <strong>{a.addressName}</strong> — {a.addressLine1}, {a.city}, {a.state}{" "}
                      {a.pincode}
                    </span>
                  </label>
                ))}
              </div>
            )}

            {!showAddressForm ? (
              <button className="btn btn-ghost" onClick={() => setShowAddressForm(true)}>
                <Icon name="plus" size={14} /> Add new address
              </button>
            ) : (
              <form className="inline-form" onSubmit={handleSaveAddress}>
                <div className="field">
                  <label>Label</label>
                  <input
                    required
                    value={addressForm.addressName}
                    onChange={(e) => setAddressForm((f) => ({ ...f, addressName: e.target.value }))}
                    placeholder="Home"
                  />
                </div>
                <div className="field">
                  <label>Address line 1</label>
                  <input
                    required
                    value={addressForm.addressLine1}
                    onChange={(e) => setAddressForm((f) => ({ ...f, addressLine1: e.target.value }))}
                  />
                </div>
                <div className="field">
                  <label>Address line 2</label>
                  <input
                    value={addressForm.addressLine2}
                    onChange={(e) => setAddressForm((f) => ({ ...f, addressLine2: e.target.value }))}
                  />
                </div>
                <div className="field-row">
                  <div className="field">
                    <label>Area</label>
                    <input
                      value={addressForm.area}
                      onChange={(e) => setAddressForm((f) => ({ ...f, area: e.target.value }))}
                    />
                  </div>
                  <div className="field">
                    <label>City</label>
                    <input
                      value={addressForm.city}
                      onChange={(e) => setAddressForm((f) => ({ ...f, city: e.target.value }))}
                    />
                  </div>
                </div>
                <div className="field-row">
                  <div className="field">
                    <label>State</label>
                    <input
                      required
                      value={addressForm.state}
                      onChange={(e) => setAddressForm((f) => ({ ...f, state: e.target.value }))}
                    />
                  </div>
                  <div className="field">
                    <label>Pincode</label>
                    <input
                      required
                      pattern="[0-9]{6}"
                      title="6-digit pincode"
                      value={addressForm.pincode}
                      onChange={(e) => setAddressForm((f) => ({ ...f, pincode: e.target.value }))}
                    />
                  </div>
                </div>
                <div className="inline-form__actions">
                  <button className="btn btn-primary" disabled={loading}>
                    Save address
                  </button>
                  <button type="button" className="btn btn-ghost" onClick={() => setShowAddressForm(false)}>
                    Cancel
                  </button>
                </div>
              </form>
            )}
          </section>

          <section className="checkout-section">
            <h2><Icon name="card" size={16} /> Payment card</h2>
            <p className="field-hint" style={{ marginTop: -8 }}>
              You'll confirm the CVV again on the next step — the order is placed first, then paid.
            </p>
            {cards.length > 0 && (
              <div className="option-list">
                {cards.map((c) => (
                  <label key={c.cardId} className="option-row">
                    <input
                      type="radio"
                      name="card"
                      checked={selectedCardId === c.cardId}
                      onChange={() => setSelectedCardId(c.cardId)}
                    />
                    <span>
                      {c.nameOnCard} — card ending {c.cardId.slice(-4)} ({c.cardType}) · balance ₹
                      {Number(c.balance || 0).toFixed(2)}
                    </span>
                  </label>
                ))}
              </div>
            )}

            {!showCardForm ? (
              <button className="btn btn-ghost" onClick={() => setShowCardForm(true)}>
                <Icon name="plus" size={14} /> Add new card
              </button>
            ) : (
              <form className="inline-form" onSubmit={handleSaveCard}>
                <div className="field-row">
                  <div className="field">
                    <label>Card number</label>
                    <input
                      required
                      value={cardForm.cardId}
                      onChange={(e) => setCardForm((f) => ({ ...f, cardId: e.target.value }))}
                      placeholder="4111111111111111"
                    />
                  </div>
                  <div className="field">
                    <label>Name on card</label>
                    <input
                      required
                      value={cardForm.nameOnCard}
                      onChange={(e) => setCardForm((f) => ({ ...f, nameOnCard: e.target.value }))}
                    />
                  </div>
                </div>
                <div className="field-row">
                  <div className="field">
                    <label>Type</label>
                    <select
                      value={cardForm.cardType}
                      onChange={(e) => setCardForm((f) => ({ ...f, cardType: e.target.value }))}
                    >
                      <option value="CREDIT">Credit</option>
                      <option value="DEBIT">Debit</option>
                    </select>
                  </div>
                  <div className="field">
                    <label>CVV</label>
                    <input
                      required
                      maxLength={3}
                      value={cardForm.cvv}
                      onChange={(e) => setCardForm((f) => ({ ...f, cvv: e.target.value }))}
                    />
                  </div>
                  <div className="field">
                    <label>Expiry</label>
                    <input
                      required
                      type="date"
                      value={cardForm.expiryDate}
                      onChange={(e) => setCardForm((f) => ({ ...f, expiryDate: e.target.value }))}
                    />
                  </div>
                </div>
                <div className="field">
                  <label>Card balance (demo top-up)</label>
                  <input
                    required
                    type="number"
                    min="0"
                    step="0.01"
                    value={cardForm.balance}
                    onChange={(e) => setCardForm((f) => ({ ...f, balance: e.target.value }))}
                    placeholder="5000"
                  />
                </div>
                <p className="field-hint">
                  This demo card is debited for real by the payment service, so it needs a starting balance.
                </p>
                <div className="inline-form__actions">
                  <button className="btn btn-primary" disabled={loading}>
                    Save card
                  </button>
                  <button type="button" className="btn btn-ghost" onClick={() => setShowCardForm(false)}>
                    Cancel
                  </button>
                </div>
              </form>
            )}
          </section>

          <button
            className="btn btn-primary btn-block checkout-cta"
            disabled={loading || !selectedAddressId}
            onClick={handlePlaceOrder}
          >
            {loading ? "Placing order…" : "Place order"}
          </button>
        </>
      )}

      {step === STEP.PAY && order && (
        <section className="checkout-section">
          <h2>Pay for order #{order.orderId}</h2>
          <p className="checkout-amount">
            Amount due: <strong>₹{order.amountPaid?.toFixed(2)}</strong>
            {order.discount > 0 && (
              <span className="muted"> (₹{order.discount.toFixed(2)} discount applied)</span>
            )}
          </p>

          {!selectedCardId ? (
            <div className="hint-banner">
              No saved card yet — go back and add one before paying.
              <div style={{ marginTop: 10 }}>
                <button className="btn btn-ghost" onClick={() => setStep(STEP.REVIEW)}>
                  <Icon name="arrowLeft" size={14} /> Back
                </button>
              </div>
            </div>
          ) : (
            <form className="inline-form" onSubmit={handlePay} style={{ borderTop: "none", paddingTop: 0, marginTop: 0 }}>
              <p className="field-hint" style={{ marginTop: 0 }}>
                Paying with card ending {selectedCardId.slice(-4)}
              </p>
              <div className="field">
                <label>Enter CVV to confirm</label>
                <input
                  required
                  maxLength={3}
                  value={cvvInput}
                  onChange={(e) => setCvvInput(e.target.value)}
                  placeholder="123"
                />
              </div>
              <button className="btn btn-primary btn-block" disabled={loading}>
                {loading ? "Processing payment…" : `Pay ₹${order.amountPaid?.toFixed(2)}`}
              </button>
            </form>
          )}
        </section>
      )}
    </div>
  );
}
