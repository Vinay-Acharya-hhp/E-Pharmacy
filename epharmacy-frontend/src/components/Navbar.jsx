import { useEffect, useRef, useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import Icon from "./Icon";
import "./Navbar.css";

export default function Navbar() {
  const { isAuthenticated, customerName, logout } = useAuth();
  const { count } = useCart();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  const menuRef = useRef(null);

  useEffect(() => {
    function onClick(e) {
      if (menuRef.current && !menuRef.current.contains(e.target)) setMenuOpen(false);
    }
    document.addEventListener("mousedown", onClick);
    return () => document.removeEventListener("mousedown", onClick);
  }, []);

  function handleLogout() {
    logout();
    setMenuOpen(false);
    setMobileOpen(false);
    navigate("/");
  }

  const firstName = customerName ? customerName.split("@")[0] : "Account";

  return (
    <header className="nav">
      <div className="container nav__inner">
        <Link to="/" className="nav__brand" onClick={() => setMobileOpen(false)}>
          <span className="nav__mark">Rx</span>
          <span className="nav__word">E-Pharmacy</span>
        </Link>

        <button
          className="nav__burger"
          aria-label="Toggle menu"
          onClick={() => setMobileOpen((v) => !v)}
        >
          <Icon name={mobileOpen ? "close" : "chevronDown"} size={22} />
        </button>

        <nav className={`nav__links ${mobileOpen ? "nav__links--open" : ""}`}>
          <NavLink to="/" end className="nav__link" onClick={() => setMobileOpen(false)}>
            Catalog
          </NavLink>
          <NavLink to="/track-order" className="nav__link" onClick={() => setMobileOpen(false)}>
            Track order
          </NavLink>
          <NavLink to="/cart" className="nav__link nav__cart" onClick={() => setMobileOpen(false)}>
            <Icon name="cart" size={17} />
            Cart
            {count > 0 && <span className="nav__cart-count">{count}</span>}
          </NavLink>

          {isAuthenticated ? (
            <div className="nav__menu" ref={menuRef}>
              <button className="nav__link nav__account" onClick={() => setMenuOpen((v) => !v)}>
                <Icon name="user" size={17} />
                {firstName}
                <Icon name="chevronDown" size={14} />
              </button>
              {menuOpen && (
                <div className="nav__dropdown">
                  <Link to="/orders" onClick={() => setMenuOpen(false)}>
                    <Icon name="box" size={16} /> Orders
                  </Link>
                  <Link to="/addresses" onClick={() => setMenuOpen(false)}>
                    <Icon name="pin" size={16} /> Addresses
                  </Link>
                  <Link to="/cards" onClick={() => setMenuOpen(false)}>
                    <Icon name="card" size={16} /> Payment methods
                  </Link>
                  <Link to="/profile" onClick={() => setMenuOpen(false)}>
                    <Icon name="edit" size={16} /> Profile &amp; security
                  </Link>
                  <button onClick={handleLogout}>
                    <Icon name="logout" size={16} /> Sign out
                  </button>
                </div>
              )}
            </div>
          ) : (
            <Link to="/login" className="btn btn-primary" onClick={() => setMobileOpen(false)}>
              Sign in
            </Link>
          )}
        </nav>
      </div>
    </header>
  );
}
