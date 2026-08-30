import { useState } from "react";
import { Link } from "react-router-dom";
import { endpoints, extractErrorMessage } from "../api/client";
import Icon from "../components/Icon";
import "./Admin.css";

const CATEGORY_OPTIONS = [
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

const STATIC_OPTIONS = [
  { label: "None (use category default)", value: "" },
  { label: "Paracetamol pack photo", value: "/images/catalog/paracetamol_500mg.jpg" },
  { label: "Vitamin B12 photo", value: "/images/catalog/vitamin_B12.webp" },
  { label: "Amla powder photo", value: "/images/catalog/Amla-powder-1.png" },
  { label: "Ayurvedic label photo", value: "/images/catalog/ayurvedic.webp" },
  { label: "English medicine strip photo", value: "/images/catalog/english.webp" },
  { label: "Homeopathy bottle photo", value: "/images/catalog/homiopati.webp" },
  { label: "Tonic bottle photo", value: "/images/catalog/tonic.webp" },
  { label: "ORS sachet photo", value: "/images/catalog/ors.webp" },
];

const emptyMedicine = {
  medicineName: "",
  manufacturer: "",
  category: "Painkiller",
  manufacturing_Date: "",
  expirey_Date: "",
  price: "",
  discountPercent: "0",
  quantity: "",
  imageUrl: "",
};

// The backend expects dd-MM-yyyy; <input type="date"> gives yyyy-mm-dd.
function toBackendDate(isoDate) {
  if (!isoDate) return null;
  const [y, m, d] = isoDate.split("-");
  return `${d}-${m}-${y}`;
}

function AddMedicineForm() {
  const [form, setForm] = useState(emptyMedicine);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [created, setCreated] = useState(null);

  function update(key, value) {
    setForm((f) => ({ ...f, [key]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setCreated(null);
    try {
      const res = await endpoints.medicine.addMedicine({
        ...form,
        manufacturing_Date: toBackendDate(form.manufacturing_Date),
        expirey_Date: toBackendDate(form.expirey_Date),
        price: Number(form.price),
        discountPercent: Number(form.discountPercent) || 0,
        quantity: Number(form.quantity),
      });
      setCreated(res.data?.data);
      setForm(emptyMedicine);
    } catch (err) {
      setError(extractErrorMessage(err, "Could not list this medicine."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <form className="panel" onSubmit={handleSubmit}>
      <h2 className="panel__title">List a new medicine</h2>
      <p className="panel__hint">POST /medicine/add-medicine — visible in the catalog immediately.</p>

      {error && <div className="error-banner">{error}</div>}
      {created && (
        <div className="hint-banner">
          Added <strong>{created.medicineName}</strong> as medicine #{created.id}.{" "}
          <Link to={`/medicine/${created.id}`} className="link-plain">View listing</Link>
        </div>
      )}

      <div className="field-row">
        <div className="field">
          <label>Medicine name</label>
          <input required value={form.medicineName} onChange={(e) => update("medicineName", e.target.value)} />
        </div>
        <div className="field">
          <label>Manufacturer</label>
          <input required value={form.manufacturer} onChange={(e) => update("manufacturer", e.target.value)} />
        </div>
      </div>

      <div className="field-row">
        <div className="field">
          <label>Category</label>
          <select value={form.category} onChange={(e) => update("category", e.target.value)}>
            {CATEGORY_OPTIONS.map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>
        </div>
        <div className="field">
          <label>Photo</label>
          <select value={form.imageUrl} onChange={(e) => update("imageUrl", e.target.value)}>
            {STATIC_OPTIONS.map((o) => (
              <option key={o.label} value={o.value}>{o.label}</option>
            ))}
          </select>
        </div>
      </div>

      <div className="field-row">
        <div className="field">
          <label>Manufactured on</label>
          <input required type="date" value={form.manufacturing_Date} onChange={(e) => update("manufacturing_Date", e.target.value)} />
        </div>
        <div className="field">
          <label>Expires on</label>
          <input required type="date" value={form.expirey_Date} onChange={(e) => update("expirey_Date", e.target.value)} />
        </div>
      </div>

      <div className="field-row">
        <div className="field">
          <label>Price (₹)</label>
          <input required type="number" min="0" step="0.01" value={form.price} onChange={(e) => update("price", e.target.value)} />
        </div>
        <div className="field">
          <label>Discount %</label>
          <input type="number" min="0" max="90" value={form.discountPercent} onChange={(e) => update("discountPercent", e.target.value)} />
        </div>
        <div className="field">
          <label>Stock quantity</label>
          <input required type="number" min="0" value={form.quantity} onChange={(e) => update("quantity", e.target.value)} />
        </div>
      </div>

      <button className="btn btn-primary" disabled={saving}>
        {saving ? "Listing…" : "List medicine"}
      </button>
    </form>
  );
}

function UpdateStockForm() {
  const [medicineId, setMedicineId] = useState("");
  const [amount, setAmount] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setMessage(null);
    try {
      await endpoints.medicine.updateStock(medicineId, Number(amount));
      setMessage(`Deducted ${amount} unit(s) from medicine #${medicineId}.`);
      setAmount("");
    } catch (err) {
      setError(extractErrorMessage(err, "Could not update stock."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <form className="panel" onSubmit={handleSubmit}>
      <h2 className="panel__title">Adjust stock</h2>
      <p className="panel__hint">
        PUT /medicine/update-stock/&#123;id&#125; — this is the same call the order service makes
        automatically once an order is confirmed; use it here for manual corrections.
      </p>

      {error && <div className="error-banner">{error}</div>}
      {message && <div className="hint-banner">{message}</div>}

      <div className="field-row">
        <div className="field">
          <label>Medicine ID</label>
          <input required inputMode="numeric" value={medicineId} onChange={(e) => setMedicineId(e.target.value)} placeholder="e.g. 12" />
        </div>
        <div className="field">
          <label>Units to deduct</label>
          <input required type="number" min="1" value={amount} onChange={(e) => setAmount(e.target.value)} placeholder="e.g. 5" />
        </div>
      </div>

      <button className="btn btn-primary" disabled={saving}>
        {saving ? "Updating…" : "Update stock"}
      </button>
    </form>
  );
}

export default function Admin() {
  const [tab, setTab] = useState("add");

  return (
    <main className="container page admin">
      <p className="page__eyebrow">Pharmacist console · pharmacy-medicine-service</p>
      <h1 className="page__title">Inventory tools</h1>
      <p className="admin__sub">
        <Icon name="flask" size={14} /> These endpoints don't carry role-based auth in the
        backend — anyone with the URL can use them. This console just gives them a UI, the same
        as the medicine catalog itself.
      </p>

      <div className="tabs">
        <button className={`tab ${tab === "add" ? "tab--active" : ""}`} onClick={() => setTab("add")}>
          Add medicine
        </button>
        <button className={`tab ${tab === "stock" ? "tab--active" : ""}`} onClick={() => setTab("stock")}>
          Update stock
        </button>
      </div>

      {tab === "add" ? <AddMedicineForm /> : <UpdateStockForm />}
    </main>
  );
}
