import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import "./Navbar.css";

export default function Navbar() {
  const { isAuthenticated, customerName, logout } = useAuth();
  const { count } = useCart();
  const navigate = useNavigate();

  return (
    <header className="nav">
      <div className="container nav__inner">
        <Link to="/" className="nav__brand">
          <span className="nav__mark">Rx</span>
          <span className="nav__word">MedRx</span>
        </Link>

        <nav className="nav__links">
          <Link to="/" className="nav__link">
            Catalog
          </Link>
          <Link to="/cart" className="nav__link nav__cart">
            Cart
            {count > 0 && <span className="nav__cart-count">{count}</span>}
          </Link>
          {isAuthenticated ? (
            <button
              className="btn btn-ghost"
              onClick={() => {
                logout();
                navigate("/");
              }}
            >
              Sign out{customerName ? ` (${customerName.split("@")[0]})` : ""}
            </button>
          ) : (
            <Link to="/login" className="btn btn-primary">
              Sign in
            </Link>
          )}
        </nav>
      </div>
    </header>
  );
}
