import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { endpoints, extractErrorMessage } from "../api/client";
import { resolveImageUrl, staticImageFor } from "../utils/medicineImages";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import Icon from "../components/Icon";
import "./MedicineDetail.css";

function formatDate(d) {
  if (!d) return "—";
  try {
    return new Date(d).toLocaleDateString(undefined, {
      day: "2-digit",
      month: "short",
      year: "numeric",
    });
  } catch {
    return d;
  }
}

export default function MedicineDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const { addToCart } = useCart();

  const [medicine, setMedicine] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [photoFailed, setPhotoFailed] = useState(false);
  const [qty, setQty] = useState(1);
  const [adding, setAdding] = useState(false);
  const [toast, setToast] = useState(null);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);
    setPhotoFailed(false);
    setQty(1);
    endpoints.medicine
      .getById(id)
      .then((res) => {
        if (!cancelled) setMedicine(res.data?.data || null);
      })
      .catch((err) => {
        if (!cancelled) setError(extractErrorMessage(err, "Could not load this medicine."));
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [id]);

  async function handleAdd() {
    if (!isAuthenticated) {
      navigate("/login", { state: { from: `/medicine/${id}` } });
      return;
    }
    setAdding(true);
    try {
      await addToCart(id, qty);
      setToast("Added to cart.");
    } catch (err) {
      setToast(extractErrorMessage(err, "Could not add to cart."));
    } finally {
      setAdding(false);
      setTimeout(() => setToast(null), 2200);
    }
  }

  if (loading) {
    return (
      <div className="container page">
        <div className="med-detail med-detail--loading">
          <div className="skeleton med-detail__photo-skel" />
          <div className="med-detail__info">
            <div className="skeleton" style={{ height: 14, width: 120, marginBottom: 14 }} />
            <div className="skeleton" style={{ height: 32, width: "70%", marginBottom: 10 }} />
            <div className="skeleton" style={{ height: 16, width: "40%" }} />
          </div>
        </div>
      </div>
    );
  }

  if (error || !medicine) {
    return (
      <div className="container page">
        <div className="empty-state">
          <div className="icon-badge">
            <Icon name="alert" />
          </div>
          <h2>Couldn't load this medicine</h2>
          <p>{error || "It may have been removed from the catalog."}</p>
          <Link to="/" className="btn btn-primary">
            Back to catalog
          </Link>
        </div>
      </div>
    );
  }

  const {
    medicineName,
    manufacturer,
    category,
    price,
    discountPercent,
    quantity,
    imageUrl,
    manufacturing_Date,
    expirey_Date,
  } = medicine;

  const src = !photoFailed && (resolveImageUrl(imageUrl) || staticImageFor(category));
  const finalPrice = discountPercent ? price - (price * discountPercent) / 100 : price;
  const outOfStock = quantity !== undefined && quantity !== null && quantity <= 0;

  return (
    <div className="container page">
      <button className="back-link" onClick={() => navigate(-1)}>
        <Icon name="arrowLeft" size={16} /> Back
      </button>

      <div className="med-detail">
        <div className="med-detail__photo">
          <img src={src} alt={medicineName} onError={() => setPhotoFailed(true)} />
          {discountPercent > 0 && <span className="med-card__badge">-{discountPercent}%</span>}
        </div>

        <div className="med-detail__info">
          <p className="page__eyebrow">{category || "General"}</p>
          <h1 className="med-detail__name">{medicineName}</h1>
          <p className="med-detail__manufacturer">by {manufacturer || "Unknown manufacturer"}</p>

          <div className="med-detail__price-row">
            <span className="med-detail__price">₹{finalPrice?.toFixed(2)}</span>
            {discountPercent > 0 && (
              <>
                <span className="med-detail__price-was">₹{price?.toFixed(2)}</span>
                <span className="pill pill--processing">{discountPercent}% off</span>
              </>
            )}
          </div>

          <div className="med-detail__facts">
            <div>
              <span className="muted">Stock</span>
              <strong className={outOfStock ? "med-detail__oos" : ""}>
                {outOfStock ? "Out of stock" : `${quantity} units available`}
              </strong>
            </div>
            <div>
              <span className="muted">Manufactured</span>
              <strong>{formatDate(manufacturing_Date)}</strong>
            </div>
            <div>
              <span className="muted">Expires</span>
              <strong>{formatDate(expirey_Date)}</strong>
            </div>
          </div>

          {!outOfStock && (
            <div className="med-detail__buy">
              <div className="qty-stepper">
                <button onClick={() => setQty((q) => Math.max(1, q - 1))} aria-label="Decrease quantity">
                  <Icon name="minus" size={14} />
                </button>
                <span>{qty}</span>
                <button
                  onClick={() => setQty((q) => Math.min(quantity ?? 99, q + 1))}
                  aria-label="Increase quantity"
                >
                  <Icon name="plus" size={14} />
                </button>
              </div>
              <button className="btn btn-primary" disabled={adding} onClick={handleAdd}>
                <Icon name="cart" size={16} />
                {adding ? "Adding…" : `Add ${qty} to cart`}
              </button>
            </div>
          )}

          {!isAuthenticated && (
            <p className="field-hint" style={{ marginTop: 14 }}>
              <Link className="link-plain" to="/login">
                Sign in
              </Link>{" "}
              to add items to your cart.
            </p>
          )}
        </div>
      </div>

      {toast && <div className="toast">{toast}</div>}
    </div>
  );
}
