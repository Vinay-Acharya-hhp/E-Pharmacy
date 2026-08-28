import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { customerApi, orderApi, paymentApi, extractErrorMessage } from "../api/client";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
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
  cartType: "CREDIT",
  cvv: "",
  expiryDate: "",
};

const STEP = { REVIEW: "review", PLACE: "place", PAY: "pay", DONE: "done" };

export default function Checkout() {
  const { customerId } = useAuth();
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
      const res = await customerApi.get("/customer/view-address");
      const list = res.data?.data || [];
      setAddresses(list);
      const firstAddressId = list[0]?.id ?? list[0]?.addressId;
      if (firstAddressId) setSelectedAddressId(Number(firstAddressId));
    } catch {
      setAddresses([]);
    }
  }

  async function loadCards() {
    try {
      const res = await paymentApi.get("/payment/getcards");
      const list = res.data?.data || [];
      setCards(list);
      if (list.length > 0) setSelectedCardId(list[0].cardId);
    } catch {
      setCards([]);
    }
  }

  async function handleSaveAddress(e) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await customerApi.post("/customer/add-address", addressForm);
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
      await paymentApi.post("/payment/addcard", cardForm);
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
      setError("Add or select a saved delivery address first. If you just updated the backend, restart user-service so address IDs are returned.");
      return;
    }
    setError(null);
    setLoading(true);
    setStep(STEP.PLACE);
    try {
      const res = await orderApi.post("/order/place-order", {
        orderValueBeforeDiscount: null,
        customer: { customerId: Number(customerId) },
        deliveryAddress: { addressId: selectedAddressId },
        card: null,
      });
      setOrder(res.data?.data);
      setStep(STEP.PAY);
    } catch (err) {
      setError(extractErrorMessage(err, "Could not place order."));
      setStep(STEP.REVIEW);
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
      await paymentApi.post(`/payment/amount/${order.amountPaid}`, {
        cardId: selectedCardId,
        nameOnCard: cards.find((c) => c.cardId === selectedCardId)?.nameOnCard,
        cardType: cards.find((c) => c.cardId === selectedCardId)?.cartType,
        cvv: cvvInput,
        orderId: order.orderId,
      });
      setStep(STEP.DONE);
      await refreshCart();
    } catch (err) {
      setError(extractErrorMessage(err, "Payment failed. Check your CVV and try again."));
    } finally {
      setLoading(false);
    }
  }

  if (step === STEP.DONE) {
    return (
      <div className="container checkout-done">
        <h1>Order confirmed</h1>
        <p>
          Order #{order?.orderId} is paid and on its way — expected delivery{" "}
          {order?.expectedDeliveryDate}.
        </p>
        <button className="btn btn-primary" onClick={() => navigate("/")}>
          Back to catalog
        </button>
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
            <h2>Delivery address</h2>
            {addresses.length > 0 && (
              <div className="option-list">
                {addresses.map((a) => (
                  <label key={a.id ?? a.addressId ?? a.addressName} className="option-row">
                    <input
                      type="radio"
                      name="address"
                      checked={selectedAddressId === Number(a.id ?? a.addressId)}
                      onChange={() => setSelectedAddressId(Number(a.id ?? a.addressId))}
                      disabled={!a.id && !a.addressId}
                    />
                    <span>
                      <strong>{a.addressName}</strong> — {a.addressLine1}, {a.city},{" "}
                      {a.state} {a.pincode}
                    </span>
                  </label>
                ))}
              </div>
            )}

            {!showAddressForm ? (
              <button className="btn btn-ghost" onClick={() => setShowAddressForm(true)}>
                + Add new address
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
                    <label>City</label>
                    <input
                      value={addressForm.city}
                      onChange={(e) => setAddressForm((f) => ({ ...f, city: e.target.value }))}
                    />
                  </div>
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
                      value={addressForm.pincode}
                      onChange={(e) => setAddressForm((f) => ({ ...f, pincode: e.target.value }))}
                    />
                  </div>
                </div>
                <div className="inline-form__actions">
                  <button className="btn btn-primary" disabled={loading}>
                    Save address
                  </button>
                  <button
                    type="button"
                    className="btn btn-ghost"
                    onClick={() => setShowAddressForm(false)}
                  >
                    Cancel
                  </button>
                </div>
              </form>
            )}
          </section>

          <section className="checkout-section">
            <h2>Payment card</h2>
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
                      {c.nameOnCard} — card ending {c.cardId.slice(-4)} ({c.cartType})
                    </span>
                  </label>
                ))}
              </div>
            )}

            {!showCardForm ? (
              <button className="btn btn-ghost" onClick={() => setShowCardForm(true)}>
                + Add new card
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
                      value={cardForm.cartType}
                      onChange={(e) => setCardForm((f) => ({ ...f, cartType: e.target.value }))}
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
                <div className="inline-form__actions">
                  <button className="btn btn-primary" disabled={loading}>
                    Save card
                  </button>
                  <button
                    type="button"
                    className="btn btn-ghost"
                    onClick={() => setShowCardForm(false)}
                  >
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
          </p>
          <form className="inline-form" onSubmit={handlePay}>
            <div className="field">
              <label>Enter CVV for the selected card to confirm</label>
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
        </section>
      )}
    </div>
  );
}
