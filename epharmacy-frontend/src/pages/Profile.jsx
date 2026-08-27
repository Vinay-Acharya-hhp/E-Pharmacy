import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { customerApi, extractErrorMessage } from "../api/client";
import { useAuth } from "../context/AuthContext";
import "./Profile.css";

const emptyProfile = {
  customerName: "",
  contactNumber: "",
  gender: "MALE",
};

export default function Profile() {
  const { isAuthenticated, customerName } = useAuth();
  const [profile, setProfile] = useState(null);
  const [form, setForm] = useState(emptyProfile);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!isAuthenticated) {
      setLoading(false);
      return;
    }

    async function loadProfile() {
      setLoading(true);
      setError(null);
      try {
        const res = await customerApi.get("/customer/profile");
        const data = res.data?.data;
        setProfile(data);
        setForm({
          customerName: data?.customerName || "",
          contactNumber: data?.contactNumber || "",
          gender: data?.gender || "MALE",
        });
      } catch (err) {
        setError(extractErrorMessage(err, "Could not load profile."));
      } finally {
        setLoading(false);
      }
    }

    loadProfile();
  }, [isAuthenticated]);

  function update(key, value) {
    setForm((current) => ({ ...current, [key]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setMessage(null);
    try {
      await customerApi.put("/customer/update-profile", form);
      setProfile((current) => ({ ...current, ...form }));
      setMessage("Profile updated.");
    } catch (err) {
      setError(extractErrorMessage(err, "Could not update profile."));
    } finally {
      setSaving(false);
    }
  }

  if (!isAuthenticated) {
    return (
      <div className="container profile-empty">
        <h1>Sign in to manage your profile</h1>
        <p>Your profile keeps delivery and contact details ready for checkout.</p>
        <Link to="/login" className="btn btn-primary">
          Sign in
        </Link>
      </div>
    );
  }

  return (
    <main className="container profile">
      <div className="profile__header">
        <div>
          <p className="profile__eyebrow">Account</p>
          <h1>{profile?.customerName || customerName || "Your profile"}</h1>
        </div>
        <Link to="/orders" className="btn btn-ghost">
          View orders
        </Link>
      </div>

      {error && <div className="error-banner">{error}</div>}
      {message && <div className="hint-banner">{message}</div>}

      {loading ? (
        <p className="profile__status">Loading profile...</p>
      ) : (
        <section className="profile-panel">
          <div className="profile-summary">
            <span>Email</span>
            <strong>{profile?.customerEmailId || "Not available"}</strong>
            <span>Date of birth</span>
            <strong>{profile?.dateOfBirth || "Not available"}</strong>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="field">
              <label htmlFor="profile-name">Full name</label>
              <input
                id="profile-name"
                required
                value={form.customerName}
                onChange={(e) => update("customerName", e.target.value)}
              />
            </div>
            <div className="field">
              <label htmlFor="profile-contact">Contact number</label>
              <input
                id="profile-contact"
                required
                pattern="[6-9][0-9]{9}"
                value={form.contactNumber}
                onChange={(e) => update("contactNumber", e.target.value)}
              />
            </div>
            <div className="field">
              <label htmlFor="profile-gender">Gender</label>
              <select
                id="profile-gender"
                value={form.gender}
                onChange={(e) => update("gender", e.target.value)}
              >
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
            <button className="btn btn-primary btn-block" disabled={saving}>
              {saving ? "Saving..." : "Save profile"}
            </button>
          </form>
        </section>
      )}
    </main>
  );
}
