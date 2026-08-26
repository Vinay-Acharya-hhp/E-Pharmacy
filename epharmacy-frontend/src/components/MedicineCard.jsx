import { useState } from "react";
import { paletteFor, monogramFor } from "../utils/medicineArt";
import "./MedicineCard.css";

function daysUntil(dateStr) {
  if (!dateStr) return null;
  const diff = new Date(dateStr).getTime() - Date.now();
  return Math.ceil(diff / (1000 * 60 * 60 * 24));
}

export default function MedicineCard({ medicine, onAddToCart, adding }) {
  const [photoFailed, setPhotoFailed] = useState(false);
  const {
    id,
    medicineName,
    manufacturer,
    category,
    price,
    discountPercent,
    imageUrl,
    expirey_Date,
  } = medicine;

  const palette = paletteFor(category || medicineName);
  const showPhoto = imageUrl && !photoFailed;
  const finalPrice = discountPercent
    ? (price - (price * discountPercent) / 100).toFixed(2)
    : price?.toFixed(2);

  const expiryDays = daysUntil(expirey_Date);
  const expiringSoon = expiryDays !== null && expiryDays > 0 && expiryDays < 60;

  return (
    <article className="med-card">
      <div className="med-card__photo" style={!showPhoto ? { background: palette.bg } : undefined}>
        {showPhoto ? (
          <img
            src={imageUrl}
            alt={medicineName}
            loading="lazy"
            onError={() => setPhotoFailed(true)}
          />
        ) : (
          <span className="med-card__monogram" style={{ color: palette.fg }}>
            {monogramFor(medicineName)}
          </span>
        )}
        {discountPercent > 0 && <span className="med-card__badge">-{discountPercent}%</span>}
      </div>

      <div className="med-card__label">
        <p className="med-card__category">{category || "General"}</p>
        <h3 className="med-card__name">{medicineName}</h3>
        <p className="med-card__manufacturer">{manufacturer}</p>

        {expiringSoon && (
          <p className="med-card__expiry">Expires in {expiryDays} days</p>
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
            onClick={() => onAddToCart(id)}
            disabled={adding}
          >
            {adding ? "Adding…" : "Add to cart"}
          </button>
        </div>
      </div>
    </article>
  );
}
