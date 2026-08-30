import { useCallback, useEffect, useState } from "react";
import { endpoints, extractErrorMessage } from "../api/client";
import MedicineCard from "../components/MedicineCard";
import CategoryChips from "../components/CategoryChips";
import Pagination from "../components/Pagination";
import Icon from "../components/Icon";
import SearchBar from "../components/SearchBar";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import "./Home.css";

const PAGE_SIZE = 8;

export default function Home() {
  const [medicines, setMedicines] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const [page, setPage] = useState(0);
  const [category, setCategory] = useState("All");
  const [searchTerm, setSearchTerm] = useState("");
  const [searchBarKey, setSearchBarKey] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [addingId, setAddingId] = useState(null);
  const [toast, setToast] = useState(null);

  const { addToCart } = useCart();
  const { isAuthenticated } = useAuth();

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      let res;
      if (searchTerm.trim()) {
        res = await endpoints.medicine.search(searchTerm.trim(), page, PAGE_SIZE);
      } else if (category !== "All") {
        res = await endpoints.medicine.getByCategory(category, page, PAGE_SIZE);
      } else {
        res = await endpoints.medicine.getAll(page, PAGE_SIZE);
      }
      const pageData = res.data?.data;
      setMedicines(pageData?.content || []);
      setTotalPages(pageData?.totalPages ?? 1);
    } catch (err) {
      setError(extractErrorMessage(err, "Could not load medicines. Is the backend running?"));
      setMedicines([]);
    } finally {
      setLoading(false);
    }
  }, [page, category, searchTerm]);

  useEffect(() => {
    load();
  }, [load]);

  function handleSearch(term) {
    setPage(0);
    setSearchTerm(term);
    if (term) setCategory("All");
  }

  function handleCategorySelect(cat) {
    setCategory(cat);
    setSearchTerm("");
    setSearchBarKey((k) => k + 1); // remount SearchBar to clear its input
    setPage(0);
  }

  async function handleAddToCart(medicineId) {
    if (!isAuthenticated) {
      setToast("Sign in to add items to your cart.");
      setTimeout(() => setToast(null), 2500);
      return;
    }
    setAddingId(medicineId);
    try {
      await addToCart(medicineId, 1);
      setToast("Added to cart.");
    } catch (err) {
      setToast(extractErrorMessage(err, "Could not add to cart."));
    } finally {
      setAddingId(null);
      setTimeout(() => setToast(null), 2000);
    }
  }

  return (
    <div className="home">
      <section className="hero">
        <div className="hero__decor" />
        <div className="container hero__inner">
          <p className="hero__eyebrow">Dispensed with care since your last order</p>
          <h1 className="hero__title">Everything on the shelf, nothing on the counter.</h1>
          <p className="hero__sub">
            Search the full formulary, filter by category, and add straight to your cart —
            every listing carries a real label photo, not a placeholder.
          </p>
          <SearchBar key={searchBarKey} onSearch={handleSearch} />
        </div>
      </section>

      <div className="container">
        <CategoryChips active={category} onSelect={handleCategorySelect} />

        {error && <div className="error-banner">{error}</div>}

        {loading ? (
          <div className="med-grid">
            {Array.from({ length: PAGE_SIZE }).map((_, i) => (
              <div className="med-card-skel" key={i}>
                <div className="skeleton med-card-skel__photo" />
                <div className="skeleton" style={{ height: 12, width: "60%", margin: "14px 0 8px" }} />
                <div className="skeleton" style={{ height: 16, width: "85%" }} />
              </div>
            ))}
          </div>
        ) : medicines.length === 0 ? (
          <div className="empty-state">
            <div className="icon-badge">
              <Icon name="search" />
            </div>
            <h3>No medicines found</h3>
            <p>Try a different search term or category.</p>
          </div>
        ) : (
          <div className="med-grid">
            {medicines.map((med) => (
              <MedicineCard
                key={med.id}
                medicine={med}
                onAddToCart={handleAddToCart}
                adding={addingId === med.id}
              />
            ))}
          </div>
        )}

        <Pagination page={page} totalPages={totalPages} onChange={setPage} />
      </div>

      {toast && <div className="toast">{toast}</div>}
    </div>
  );
}
