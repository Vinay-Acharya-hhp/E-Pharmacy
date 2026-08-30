import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { endpoints, extractErrorMessage } from "../api/client";
import { useAuth } from "../context/AuthContext";
import Icon from "../components/Icon";
import "./Addresses.css";

const emptyAddress = {
  addressName: "",
  addressLine1: "",
  addressLine2: "",
  area: "",
  city: "",
  state: "",
  pincode: "",
};

export default function Addresses() {
  const { isAuthenticated } = useAuth();
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyAddress);
  const [saving, setSaving] = useState(false);

  const [expandedId, setExpandedId] = useState(null);
  const [detail, setDetail] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState(null);

  async function load() {
    setLoading(true);
    setError(null);
    try {
      const res = await endpoints.customer.viewAddress();
      setAddresses(res.data?.data || []);
    } catch (err) {
      setError(extractErrorMessage(err, "Could not load your addresses."));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (isAuthenticated) load();
    else setLoading(false);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated]);

  async function handleSave(e) {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      await endpoints.customer.addAddress(form);
      setForm(emptyAddress);
      setShowForm(false);
      await load();
    } catch (err) {
      setError(extractErrorMessage(err, "Could not save this address."));
    } finally {
      setSaving(false);
    }
  }

  async function toggleDetail(addressId) {
    if (expandedId === addressId) {
      setExpandedId(null);
      setDetail(null);
      return;
    }
    setExpandedId(addressId);
    setDetail(null);
    setDetailError(null);
    setDetailLoading(true);
    try {
      // Fetches a fresh copy straight from GET /customer/getaddress/{id},
      // separate from the list already loaded above.
      const res = await endpoints.customer.getAddress(addressId);
      setDetail(res.data?.data);
    } catch (err) {
      setDetailError(extractErrorMessage(err, "Could not load this address."));
    } finally {
      setDetailLoading(false);
    }
  }

  if (!isAuthenticated) {
    return (
      <div className="container page">
        <div className="empty-state">
          <div className="icon-badge"><Icon name="pin" size={22} /></div>
          <h2>Sign in to manage addresses</h2>
          <p>Delivery addresses are saved to your account.</p>
          <Link to="/login" className="btn btn-primary">Sign in</Link>
        </div>
      </div>
    );
  }

  return (
    <main className="container page addresses">
      <div className="page__header">
        <div>
          <p className="page__eyebrow">Account · pharmacy-user-service</p>
          <h1 className="page__title">Delivery addresses</h1>
        </div>
        <button className="btn btn-primary" onClick={() => setShowForm((v) => !v)}>
          <Icon name="plus" size={15} /> {showForm ? "Close" : "Add address"}
        </button>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {showForm && (
        <form className="panel" onSubmit={handleSave}>
          <h2 className="panel__title">New address</h2>
          <div className="field">
            <label>Label</label>
            <input
              required
              value={form.addressName}
              onChange={(e) => setForm((f) => ({ ...f, addressName: e.target.value }))}
              placeholder="Home, Office…"
            />
          </div>
          <div className="field">
            <label>Address line 1</label>
            <input
              required
              value={form.addressLine1}
              onChange={(e) => setForm((f) => ({ ...f, addressLine1: e.target.value }))}
            />
          </div>
          <div className="field">
            <label>Address line 2</label>
            <input
              value={form.addressLine2}
              onChange={(e) => setForm((f) => ({ ...f, addressLine2: e.target.value }))}
            />
          </div>
          <div className="field-row">
            <div className="field">
              <label>Area</label>
              <input value={form.area} onChange={(e) => setForm((f) => ({ ...f, area: e.target.value }))} />
            </div>
            <div className="field">
              <label>City</label>
              <input value={form.city} onChange={(e) => setForm((f) => ({ ...f, city: e.target.value }))} />
            </div>
          </div>
          <div className="field-row">
            <div className="field">
              <label>State</label>
              <input
                required
                value={form.state}
                onChange={(e) => setForm((f) => ({ ...f, state: e.target.value }))}
              />
            </div>
            <div className="field">
              <label>Pincode</label>
              <input
                required
                pattern="[0-9]{6}"
                title="6-digit pincode"
                value={form.pincode}
                onChange={(e) => setForm((f) => ({ ...f, pincode: e.target.value }))}
              />
            </div>
          </div>
          <button className="btn btn-primary" disabled={saving}>
            {saving ? "Saving…" : "Save address"}
          </button>
        </form>
      )}

      {loading ? (
        <p className="muted">Loading addresses…</p>
      ) : addresses.length === 0 ? (
        <div className="empty-state">
          <h2>No addresses yet</h2>
          <p>Add one so checkout knows where to deliver.</p>
        </div>
      ) : (
        <div className="stack-list">
          {addresses.map((a) => (
            <div className="list-row addresses__row" key={a.id}>
              <div className="list-row__main">
                <span className="list-row__icon"><Icon name="pin" size={17} /></span>
                <span>
                  <p className="list-row__title">{a.addressName}</p>
                  <p className="list-row__sub">
                    {a.addressLine1}, {a.city}, {a.state} {a.pincode}
                  </p>
                </span>
              </div>
              <div className="list-row__actions">
                <button className="btn btn-ghost" onClick={() => toggleDetail(a.id)}>
                  {expandedId === a.id ? "Hide" : "View details"}
                </button>
              </div>

              {expandedId === a.id && (
                <div className="addresses__detail">
                  {detailLoading && <p className="muted">Fetching full record…</p>}
                  {detailError && <div className="error-banner">{detailError}</div>}
                  {detail && (
                    <dl className="addresses__facts">
                      <div><dt>Address ID</dt><dd>#{detail.id}</dd></div>
                      <div><dt>Label</dt><dd>{detail.addressName}</dd></div>
                      <div><dt>Line 1</dt><dd>{detail.addressLine1 || "—"}</dd></div>
                      <div><dt>Line 2</dt><dd>{detail.addressLine2 || "—"}</dd></div>
                      <div><dt>Area</dt><dd>{detail.area || "—"}</dd></div>
                      <div><dt>City</dt><dd>{detail.city || "—"}</dd></div>
                      <div><dt>State</dt><dd>{detail.state}</dd></div>
                      <div><dt>Pincode</dt><dd>{detail.pincode}</dd></div>
                    </dl>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </main>
  );
}
