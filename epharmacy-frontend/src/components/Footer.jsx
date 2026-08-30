import { Link } from "react-router-dom";
import Icon from "./Icon";
import "./Footer.css";

export default function Footer() {
  return (
    <footer className="foot">
      <div className="container foot__inner">
        <div className="foot__brand">
          <span className="nav__mark">Rx</span>
          <div>
            <p className="foot__word">E-Pharmacy</p>
            <p className="foot__tag">Dispensed with care, delivered with the receipt attached.</p>
          </div>
        </div>

        <div className="foot__cols">
          <div className="foot__col">
            <h4>Shop</h4>
            <Link to="/">Full catalog</Link>
            <Link to="/cart">Your cart</Link>
            <Link to="/orders">Order history</Link>
            <Link to="/track-order">Track an order</Link>
          </div>
          <div className="foot__col">
            <h4>Account</h4>
            <Link to="/profile">Profile &amp; security</Link>
            <Link to="/addresses">Delivery addresses</Link>
            <Link to="/cards">Payment methods</Link>
          </div>
          <div className="foot__col">
            <h4>Operations</h4>
            <Link to="/admin">
              <Icon name="flask" size={14} /> Pharmacist console
            </Link>
            <span className="foot__note">Add stock &amp; list new medicines</span>
          </div>
        </div>
      </div>
      <div className="container foot__legal">
        <span>© {new Date().getFullYear()} E-Pharmacy — a demo storefront for a Spring Boot microservices backend.</span>
      </div>
    </footer>
  );
}
