import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import { endpoints, extractErrorMessage } from "../api/client";
import { resolveImageUrl, staticImageFor } from "../utils/medicineImages";
import Icon from "../components/Icon";
import "./Cart.css";

export default function Cart() {
  const { isAuthenticated } = useAuth();
  const { items, loading, error, refresh, updateQuantity, removeFromCart, clearCart } =
    useCart();
  const [details, setDetails] = useState({});
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [actionError, setActionError] = useState(null);

  useEffect(() => {
    if (isAuthenticated) refresh();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated]);

  useEffect(() => {
    const missingIds = items.map((i) => i.medicineId).filter((id) => !details[id]);
    if (missingIds.length === 0) return;

    setDetailsLoading(true);
    Promise.all(
      missingIds.map((id) =>
        endpoints.medicine
          .getById(id)
          .then((res) => [id, res.data?.data])
          .catch(() => [id, null])
      )
    ).then((pairs) => {
      setDetails((prev) => {
        const next = { ...prev };
        pairs.forEach(([id, data]) => {
          if (data) next[id] = data;
        });
        return next;
      });
      setDetailsLoading(false);
    });
  }, [items, details]);

  if (!isAuthenticated) {
    return (
      <div className="container page">
        <div className="empty-state">
          <div className="icon-badge"><Icon name="cart" size={24} /></div>
          <h2>Sign in to view your cart</h2>
          <p>Your cart items are saved to your account.</p>
          <Link to="/login" className="btn btn-primary">Sign in</Link>
        </div>
      </div>
    );
  }

  const rows = items.map((item) => ({ item, med: details[item.medicineId] }));
  const total = rows.reduce((sum, { item, med }) => {
    if (!med) return sum;
    const unit = med.discountPercent ? med.price - (med.price * med.discountPercent) / 100 : med.price;
    return sum + unit * item.quantity;
  }, 0);

  async function handleAction(fn) {
    setActionError(null);
    try {
      await fn();
    } catch (err) {
      setActionError(extractErrorMessage(err, "That action failed. Please try again."));
    }
  }

  return (
    <div className="container page cart">
      <div className="page__header">
        <div>
          <p className="page__eyebrow">Cart · pharmacy-cart-service</p>
          <h1 className="page__title">Your cart</h1>
        </div>
        {items.length > 0 && (
          <button className="btn btn-ghost" onClick={() => handleAction(clearCart)}>
            <Icon name="trash" size={15} /> Empty cart
          </button>
        )}
      </div>

      {(error || actionError) && <div className="error-banner">{error || actionError}</div>}

      {loading || detailsLoading ? (
        <p className="cart__status">Loading your cart…</p>
      ) : items.length === 0 ? (
        <div className="empty-state">
          <div className="icon-badge"><Icon name="cart" size={24} /></div>
          <h2>Your cart is empty</h2>
          <p>Browse the catalog and add something to your cart.</p>
          <Link to="/" className="btn btn-primary">Go to catalog</Link>
        </div>
      ) : (
        <>
          <div className="cart__list">
            {rows.map(({ item, med }) => {
              const src = resolveImageUrl(med?.imageUrl) || staticImageFor(med?.category);
              return (
                <div className="cart-row" key={item.medicineId}>
                  <Link to={`/medicine/${item.medicineId}`} className="cart-row__photo">
                    <img src={src} alt={med?.medicineName || "Medicine"} />
                  </Link>

                  <div className="cart-row__info">
                    <Link to={`/medicine/${item.medicineId}`}>
                      <h3>{med?.medicineName || `Medicine #${item.medicineId}`}</h3>
                    </Link>
                    <p>{med?.manufacturer}</p>
                  </div>

                  <div className="cart-row__qty">
                    <button
                      className="btn btn-ghost"
                      disabled={item.quantity <= 1}
                      onClick={() => handleAction(() => updateQuantity(item.medicineId, item.quantity - 1))}
                      aria-label="Decrease quantity"
                    >
                      <Icon name="minus" size={13} />
                    </button>
                    <span>{item.quantity}</span>
                    <button
                      className="btn btn-ghost"
                      onClick={() => handleAction(() => updateQuantity(item.medicineId, item.quantity + 1))}
                      aria-label="Increase quantity"
                    >
                      <Icon name="plus" size={13} />
                    </button>
                  </div>

                  <div className="cart-row__price">
                    {med
                      ? `₹${(
                          (med.discountPercent ? med.price - (med.price * med.discountPercent) / 100 : med.price) *
                          item.quantity
                        ).toFixed(2)}`
                      : "—"}
                  </div>

                  <button
                    className="cart-row__remove"
                    onClick={() => handleAction(() => removeFromCart(item.medicineId))}
                    aria-label="Remove item"
                  >
                    <Icon name="close" size={16} />
                  </button>
                </div>
              );
            })}
          </div>

          <div className="cart__summary">
            <span>Total</span>
            <span className="cart__total">₹{total.toFixed(2)}</span>
          </div>
          <Link to="/checkout" className="btn btn-primary btn-block cart__checkout">
            Proceed to checkout
          </Link>
        </>
      )}
    </div>
  );
}
