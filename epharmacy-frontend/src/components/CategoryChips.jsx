import "./CategoryChips.css";

export const CATEGORIES = [
  "Painkiller",
  "Antibiotic",
  "Vitamin",
  "Supplement",
  "Cardiac",
  "Diabetes",
  "Skincare",
  "Cold & Flu",
  "Digestive",
  "Ayurvedic",
  "Homeopathy",
];

const CHIP_OPTIONS = ["All", ...CATEGORIES];

export default function CategoryChips({ active, onSelect }) {
  return (
    <div className="chips">
      {CHIP_OPTIONS.map((cat) => (
        <button
          key={cat}
          className={`chip ${active === cat ? "chip--active" : ""}`}
          onClick={() => onSelect(cat)}
        >
          {cat}
        </button>
      ))}
    </div>
  );
}
