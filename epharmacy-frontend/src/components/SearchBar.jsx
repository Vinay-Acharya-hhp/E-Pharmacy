import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { endpoints } from "../api/client";
import { resolveImageUrl, staticImageFor } from "../utils/medicineImages";
import Icon from "./Icon";
import "./SearchBar.css";

const RECENT_KEY = "epharmacy_recent_searches";
const MAX_RECENT = 6;
const SUGGESTION_LIMIT = 6;
const DEBOUNCE_MS = 250;

function loadRecent() {
  try {
    const raw = localStorage.getItem(RECENT_KEY);
    const parsed = raw ? JSON.parse(raw) : [];
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

function saveRecent(list) {
  try {
    localStorage.setItem(RECENT_KEY, JSON.stringify(list));
  } catch {
    // localStorage unavailable — recent search history just won't persist
  }
}

/**
 * A self-contained search bar for the catalog with:
 *  - debounced live suggestions (from the medicine search endpoint)
 *  - keyboard navigation (Up/Down/Enter/Escape)
 *  - a clear ("x") button once there's text
 *  - recent searches remembered per-browser and offered on focus
 *  - clicking a suggestion goes straight to that medicine's detail page
 */
export default function SearchBar({ onSearch }) {
  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [recent, setRecent] = useState(() => loadRecent());
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [activeIndex, setActiveIndex] = useState(-1);

  const navigate = useNavigate();
  const wrapRef = useRef(null);
  const debounceRef = useRef(null);
  const requestIdRef = useRef(0);

  const trimmed = query.trim();

  const fetchSuggestions = useCallback(async (term) => {
    const requestId = ++requestIdRef.current;
    setLoading(true);
    try {
      const res = await endpoints.medicine.search(term, 0, SUGGESTION_LIMIT);
      if (requestId !== requestIdRef.current) return; // a newer keystroke won
      setSuggestions(res.data?.data?.content || []);
    } catch {
      if (requestId === requestIdRef.current) setSuggestions([]);
    } finally {
      if (requestId === requestIdRef.current) setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);
    if (!trimmed) {
      setSuggestions([]);
      setLoading(false);
      return;
    }
    debounceRef.current = setTimeout(() => fetchSuggestions(trimmed), DEBOUNCE_MS);
    return () => clearTimeout(debounceRef.current);
  }, [trimmed, fetchSuggestions]);

  useEffect(() => {
    function onClickOutside(e) {
      if (wrapRef.current && !wrapRef.current.contains(e.target)) setOpen(false);
    }
    document.addEventListener("mousedown", onClickOutside);
    return () => document.removeEventListener("mousedown", onClickOutside);
  }, []);

  const showRecent = trimmed.length === 0 && recent.length > 0;
  const listLength = showRecent ? recent.length : suggestions.length;

  function commitSearch(term) {
    const value = term.trim();
    if (!value) return;
    const next = [value, ...recent.filter((r) => r.toLowerCase() !== value.toLowerCase())].slice(
      0,
      MAX_RECENT
    );
    setRecent(next);
    saveRecent(next);
    setQuery(value);
    setOpen(false);
    setActiveIndex(-1);
    onSearch(value);
  }

  function handleSubmit(e) {
    e.preventDefault();
    if (activeIndex >= 0 && listLength > 0) {
      if (showRecent) commitSearch(recent[activeIndex]);
      else goToMedicine(suggestions[activeIndex]);
      return;
    }
    commitSearch(query);
  }

  function goToMedicine(medicine) {
    const next = [
      medicine.medicineName,
      ...recent.filter((r) => r.toLowerCase() !== medicine.medicineName.toLowerCase()),
    ].slice(0, MAX_RECENT);
    setRecent(next);
    saveRecent(next);
    setOpen(false);
    setActiveIndex(-1);
    navigate(`/medicine/${medicine.id}`);
  }

  function handleKeyDown(e) {
    if (!open && (e.key === "ArrowDown" || e.key === "ArrowUp")) {
      setOpen(true);
      return;
    }
    if (!open || listLength === 0) return;

    if (e.key === "ArrowDown") {
      e.preventDefault();
      setActiveIndex((i) => (i + 1) % listLength);
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setActiveIndex((i) => (i <= 0 ? listLength - 1 : i - 1));
    } else if (e.key === "Escape") {
      setOpen(false);
      setActiveIndex(-1);
    }
  }

  function clearQuery() {
    setQuery("");
    setSuggestions([]);
    setActiveIndex(-1);
    onSearch("");
  }

  function removeRecent(term, e) {
    e.stopPropagation();
    const next = recent.filter((r) => r !== term);
    setRecent(next);
    saveRecent(next);
  }

  const placeholder = useMemo(
    () => "Search by medicine, symptom, or manufacturer…",
    []
  );

  return (
    <div className="searchbar" ref={wrapRef}>
      <form
        className="searchbar__form"
        onSubmit={handleSubmit}
        role="combobox"
        aria-expanded={open}
        aria-owns="searchbar-listbox"
        aria-haspopup="listbox"
      >
        <Icon name="search" size={17} />
        <input
          type="text"
          value={query}
          placeholder={placeholder}
          onChange={(e) => {
            setQuery(e.target.value);
            setOpen(true);
            setActiveIndex(-1);
          }}
          onFocus={() => setOpen(true)}
          onKeyDown={handleKeyDown}
          aria-autocomplete="list"
          aria-controls="searchbar-listbox"
        />
        {loading && <span className="searchbar__spinner" aria-hidden="true" />}
        {query && !loading && (
          <button
            type="button"
            className="searchbar__clear"
            aria-label="Clear search"
            onClick={clearQuery}
          >
            <Icon name="close" size={14} />
          </button>
        )}
        <button type="submit" className="btn btn-amber">
          Search
        </button>
      </form>

      {open && (showRecent || trimmed.length > 0) && (
        <div className="searchbar__panel" id="searchbar-listbox" role="listbox">
          {showRecent && (
            <>
              <p className="searchbar__panel-label">Recent searches</p>
              {recent.map((term, i) => (
                <button
                  type="button"
                  key={term}
                  role="option"
                  aria-selected={activeIndex === i}
                  className={`searchbar__row searchbar__row--recent ${
                    activeIndex === i ? "is-active" : ""
                  }`}
                  onMouseEnter={() => setActiveIndex(i)}
                  onClick={() => commitSearch(term)}
                >
                  <Icon name="clock" size={15} />
                  <span className="searchbar__row-text">{term}</span>
                  <span
                    className="searchbar__row-remove"
                    onClick={(e) => removeRecent(term, e)}
                    role="button"
                    aria-label={`Remove ${term} from recent searches`}
                  >
                    <Icon name="close" size={12} />
                  </span>
                </button>
              ))}
            </>
          )}

          {!showRecent && trimmed.length > 0 && (
            <>
              {loading && suggestions.length === 0 && (
                <p className="searchbar__panel-label">Searching…</p>
              )}

              {!loading && suggestions.length === 0 && (
                <p className="searchbar__empty">
                  No matches for “{trimmed}”. Press Enter to search anyway.
                </p>
              )}

              {suggestions.length > 0 && (
                <>
                  <p className="searchbar__panel-label">Medicines</p>
                  {suggestions.map((med, i) => {
                    const img = resolveImageUrl(med.imageUrl) || staticImageFor(med.category);
                    return (
                      <button
                        type="button"
                        key={med.id}
                        role="option"
                        aria-selected={activeIndex === i}
                        className={`searchbar__row ${activeIndex === i ? "is-active" : ""}`}
                        onMouseEnter={() => setActiveIndex(i)}
                        onClick={() => goToMedicine(med)}
                      >
                        <img className="searchbar__thumb" src={img} alt="" />
                        <span className="searchbar__row-text">
                          <span className="searchbar__row-name">{med.medicineName}</span>
                          <span className="searchbar__row-meta">
                            {med.category} · ₹{med.price?.toFixed(2)}
                          </span>
                        </span>
                        {med.quantity <= 0 && (
                          <span className="searchbar__row-tag">Out of stock</span>
                        )}
                      </button>
                    );
                  })}
                  <button
                    type="button"
                    className="searchbar__row searchbar__row--all"
                    onClick={() => commitSearch(trimmed)}
                  >
                    <Icon name="trending" size={15} />
                    <span className="searchbar__row-text">
                      See all results for “{trimmed}”
                    </span>
                  </button>
                </>
              )}
            </>
          )}
        </div>
      )}
    </div>
  );
}
