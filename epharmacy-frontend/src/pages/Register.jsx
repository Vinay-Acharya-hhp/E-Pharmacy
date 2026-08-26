import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Auth.css";

const initialForm = {
  customerName: "",
  customerEmailId: "",
  contactNumber: "",
  password: "",
  gender: "MALE",
  dateOfBirth: "",
};

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  function update(key, value) {
    setForm((f) => ({ ...f, [key]: value }));
  }

  // Backend expects dates as dd-MM-yyyy; the input gives yyyy-mm-dd.
  function toBackendDate(isoDate) {
    if (!isoDate) return null;
    const [y, m, d] = isoDate.split("-");
    return `${d}-${m}-${y}`;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await register({
        ...form,
        dateOfBirth: toBackendDate(form.dateOfBirth),
        address: [],
      });
      setSuccess(true);
      setTimeout(() => navigate("/login"), 1200);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth">
      <div className="auth__card">
        <p className="auth__eyebrow">First visit</p>
        <h1 className="auth__title">Create your account</h1>

        {error && <div className="error-banner">{error}</div>}
        {success && (
          <div className="hint-banner">Account created — redirecting to sign in…</div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="name">Full name</label>
            <input
              id="name"
              required
              value={form.customerName}
              onChange={(e) => update("customerName", e.target.value)}
              placeholder="Jane Doe"
            />
          </div>
          <div className="field">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              required
              value={form.customerEmailId}
              onChange={(e) => update("customerEmailId", e.target.value)}
              placeholder="you@example.com"
            />
          </div>
          <div className="field">
            <label htmlFor="contact">Contact number</label>
            <input
              id="contact"
              required
              pattern="[6-9][0-9]{9}"
              title="10-digit number starting with 6-9"
              value={form.contactNumber}
              onChange={(e) => update("contactNumber", e.target.value)}
              placeholder="9876543210"
            />
          </div>
          <div className="field">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              required
              minLength={6}
              maxLength={20}
              value={form.password}
              onChange={(e) => update("password", e.target.value)}
              placeholder="At least 6 characters"
            />
          </div>
          <div className="field">
            <label htmlFor="gender">Gender</label>
            <select
              id="gender"
              value={form.gender}
              onChange={(e) => update("gender", e.target.value)}
            >
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
              <option value="OTHER">Other</option>
            </select>
          </div>
          <div className="field">
            <label htmlFor="dob">Date of birth</label>
            <input
              id="dob"
              type="date"
              required
              value={form.dateOfBirth}
              onChange={(e) => update("dateOfBirth", e.target.value)}
            />
          </div>
          <button className="btn btn-primary btn-block" disabled={loading}>
            {loading ? "Creating account…" : "Create account"}
          </button>
        </form>

        <p className="auth__switch">
          Already registered? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </div>
  );
}
