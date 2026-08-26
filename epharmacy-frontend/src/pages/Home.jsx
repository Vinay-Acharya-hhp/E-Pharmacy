import { useCallback, useEffect, useState } from "react";
import { medicineApi, extractErrorMessage } from "../api/client";
import MedicineCard from "../components/MedicineCard";
import CategoryChips from "../components/CategoryChips";
import Pagination from "../components/Pagination";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import "./Home.css";

const PAGE_SIZE = 8;

export default function Home() {
  const [medicines, setMedicines] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const [page, setPage] = useState(0);
  const [category, setCategory] = useState("All");
  const [query, setQuery] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
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
        res = await medicineApi.get(
          `/medicine/serach/${encodeURIComponent(searchTerm.trim())}/${page}`,
          { params: { size: PAGE_SIZE } }
        );
      } else if (category !== "All") {
        res = await medicineApi.get(
          `/medicine/get-category/${encodeURIComponent(category)}/${page}`,
          { params: { size: PAGE_SIZE } }
        );
      } else {
        res = await medicineApi.get(`/medicine/get-all/${page}`, {
          params: { size: PAGE_SIZE },
        });
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

  function handleSearchSubmit(e) {
    e.preventDefault();
    setPage(0);
    setSearchTerm(query);
  }

  function handleCategorySelect(cat) {
    setCategory(cat);
    setQuery("");
    setSearchTerm("");
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
        <div className="container hero__inner">
          <p className="hero__eyebrow">Dispensed with care since your last order</p>
          <h1 className="hero__title">Everything on the shelf, nothing on the counter.</h1>
          <p className="hero__sub">
            Search the full formulary, filter by category, and add straight to your cart —
            every listing carries its own label photo.
          </p>
          <form className="hero__search" onSubmit={handleSearchSubmit}>
            <input
              type="text"
              placeholder="Search by medicine name…"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
            <button type="submit" className="btn btn-amber">
              Search
            </button>
          </form>
        </div>
      </section>

      <div className="container">
        <CategoryChips active={category} onSelect={handleCategorySelect} />

        {error && <div className="error-banner">{error}</div>}

        {loading ? (
          <div className="home__loading">Reading the shelf labels…</div>
        ) : medicines.length === 0 ? (
          <div className="home__empty">
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
