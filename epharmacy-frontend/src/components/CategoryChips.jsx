import "./CategoryChips.css";

const CATEGORIES = [
  "All",
  "Painkiller",
  "Antibiotic",
  "Vitamin",
  "Supplement",
  "Cardiac",
  "Diabetes",
  "Skincare",
  "Cold & Flu",
  "Digestive",
];

export default function CategoryChips({ active, onSelect }) {
  return (
    <div className="chips">
      {CATEGORIES.map((cat) => (
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
