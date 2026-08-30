import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { resolveImageUrl, staticImageFor } from "../utils/medicineImages";
import Icon from "./Icon";
import "./MedicineCard.css";

function daysUntil(dateStr) {
  if (!dateStr) return null;
  const diff = new Date(dateStr).getTime() - Date.now();
  return Math.ceil(diff / (1000 * 60 * 60 * 24));
}

export default function MedicineCard({ medicine, onAddToCart, adding }) {
  const navigate = useNavigate();
  const [photoFailed, setPhotoFailed] = useState(false);
  const {
    id,
    medicineName,
    manufacturer,
    category,
    price,
    discountPercent,
    imageUrl,
    quantity,
    expirey_Date,
  } = medicine;

const src = !photoFailed && resolveImageUrl(imageUrl) || staticImageFor(category);
  const finalPrice = discountPercent
    ? (price - (price * discountPercent) / 100).toFixed(2)
    : price?.toFixed(2);

  const expiryDays = daysUntil(expirey_Date);
  const expiringSoon = expiryDays !== null && expiryDays > 0 && expiryDays < 60;
  const outOfStock = quantity !== undefined && quantity !== null && quantity <= 0;

  return (
    <article className="med-card" onClick={() => navigate(`/medicine/${id}`)}>
      <div className="med-card__photo">
        <img src={src} alt={medicineName} loading="lazy" onError={() => setPhotoFailed(true)} />
        {discountPercent > 0 && <span className="med-card__badge">-{discountPercent}%</span>}
        {outOfStock && <span className="med-card__badge med-card__badge--out">Out of stock</span>}
      </div>

      <div className="med-card__label">
        <p className="med-card__category">{category || "General"}</p>
        <h3 className="med-card__name">{medicineName}</h3>
        <p className="med-card__manufacturer">{manufacturer}</p>

        {expiringSoon && (
          <p className="med-card__expiry">
            <Icon name="alert" size={13} /> Expires in {expiryDays} days
          </p>
        )}

        <div className="med-card__footer">
          <div className="med-card__price">
            <span className="med-card__price-now">₹{finalPrice}</span>
            {discountPercent > 0 && (
              <span className="med-card__price-was">₹{price?.toFixed(2)}</span>
            )}
          </div>
          <button
            className="btn btn-primary"
            disabled={adding || outOfStock}
            onClick={(e) => {
              e.stopPropagation();
              onAddToCart(id);
            }}
          >
            {outOfStock ? "Unavailable" : adding ? "Adding…" : "Add to cart"}
          </button>
        </div>
      </div>
    </article>
  );
}
