import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { endpoints, extractErrorMessage } from "../api/client";
import { useAuth } from "../context/AuthContext";
import Icon from "../components/Icon";
import "./Profile.css";

const emptyProfile = { customerName: "", contactNumber: "", gender: "MALE" };
const emptyPasswordForm = { oldPassword: "", newPassword: "", confiremPassword: "" };

export default function Profile() {
  const { isAuthenticated, customerName } = useAuth();
  const [profile, setProfile] = useState(null);
  const [form, setForm] = useState(emptyProfile);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  const [pwForm, setPwForm] = useState(emptyPasswordForm);
  const [pwSaving, setPwSaving] = useState(false);
  const [pwMessage, setPwMessage] = useState(null);
  const [pwError, setPwError] = useState(null);

  useEffect(() => {
    if (!isAuthenticated) {
      setLoading(false);
      return;
    }
    async function loadProfile() {
      setLoading(true);
      setError(null);
      try {
        const res = await endpoints.customer.profile();
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
      await endpoints.customer.updateProfile(form);
      setProfile((current) => ({ ...current, ...form }));
      setMessage("Profile updated.");
    } catch (err) {
      setError(extractErrorMessage(err, "Could not update profile."));
    } finally {
      setSaving(false);
    }
  }

  async function handlePasswordSubmit(e) {
    e.preventDefault();
    setPwSaving(true);
    setPwError(null);
    setPwMessage(null);
    try {
      await endpoints.customer.changePassword(pwForm);
      setPwMessage("Password changed.");
      setPwForm(emptyPasswordForm);
    } catch (err) {
      setPwError(extractErrorMessage(err, "Could not change password."));
    } finally {
      setPwSaving(false);
    }
  }

  if (!isAuthenticated) {
    return (
      <div className="container page">
        <div className="empty-state">
          <div className="icon-badge"><Icon name="user" size={22} /></div>
          <h2>Sign in to manage your profile</h2>
          <p>Your profile keeps delivery and contact details ready for checkout.</p>
          <Link to="/login" className="btn btn-primary">Sign in</Link>
        </div>
      </div>
    );
  }

  return (
    <main className="container page profile">
      <div className="page__header">
        <div>
          <p className="page__eyebrow">Account</p>
          <h1 className="page__title">{profile?.customerName || customerName || "Your profile"}</h1>
        </div>
        <Link to="/orders" className="btn btn-ghost">View orders</Link>
      </div>

      <div className="profile__quicklinks">
        <Link to="/addresses" className="list-row list-row--link">
          <span className="list-row__main">
            <span className="list-row__icon"><Icon name="pin" size={17} /></span>
            <span>
              <p className="list-row__title">Delivery addresses</p>
              <p className="list-row__sub">Manage where your orders ship to</p>
            </span>
          </span>
          <Icon name="chevronRight" size={18} />
        </Link>
        <Link to="/cards" className="list-row list-row--link">
          <span className="list-row__main">
            <span className="list-row__icon"><Icon name="card" size={17} /></span>
            <span>
              <p className="list-row__title">Payment methods</p>
              <p className="list-row__sub">Saved cards used at checkout</p>
            </span>
          </span>
          <Icon name="chevronRight" size={18} />
        </Link>
      </div>

      {error && <div className="error-banner">{error}</div>}
      {message && <div className="hint-banner">{message}</div>}

      {loading ? (
        <p className="profile__status">Loading profile…</p>
      ) : (
        <>
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
              <button className="btn btn-primary" disabled={saving}>
                {saving ? "Saving…" : "Save profile"}
              </button>
            </form>
          </section>

          <section className="panel">
            <h2 className="panel__title"><Icon name="lock" size={17} /> Security</h2>
            <p className="panel__hint">Change your account password.</p>

            {pwError && <div className="error-banner">{pwError}</div>}
            {pwMessage && <div className="hint-banner">{pwMessage}</div>}

            <form onSubmit={handlePasswordSubmit}>
              <div className="field">
                <label>Current password</label>
                <input
                  required
                  type="password"
                  value={pwForm.oldPassword}
                  onChange={(e) => setPwForm((f) => ({ ...f, oldPassword: e.target.value }))}
                />
              </div>
              <div className="field-row">
                <div className="field">
                  <label>New password</label>
                  <input
                    required
                    type="password"
                    minLength={6}
                    maxLength={20}
                    value={pwForm.newPassword}
                    onChange={(e) => setPwForm((f) => ({ ...f, newPassword: e.target.value }))}
                  />
                </div>
                <div className="field">
                  <label>Confirm new password</label>
                  <input
                    required
                    type="password"
                    minLength={6}
                    maxLength={20}
                    value={pwForm.confiremPassword}
                    onChange={(e) => setPwForm((f) => ({ ...f, confiremPassword: e.target.value }))}
                  />
                </div>
              </div>
              <button className="btn btn-primary" disabled={pwSaving}>
                {pwSaving ? "Updating…" : "Change password"}
              </button>
            </form>
          </section>
        </>
      )}
    </main>
  );
}
