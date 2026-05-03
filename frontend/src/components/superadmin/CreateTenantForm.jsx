import { useState } from "react";
import { createTenant } from "../../api";

const INITIAL_FORM = {
  tenantName: "",
  tenantCategory: "",
  adminFullName: "",
  adminEmail: "",
};

function CreateTenantForm({ onBack, onCreated }) {
  const [form, setForm] = useState(INITIAL_FORM);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [result, setResult] = useState(null);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const data = await createTenant(form);
      setResult(data);
      if (onCreated) onCreated();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to create tenant.");
    } finally {
      setLoading(false);
    }
  };

  // ── Success screen ────────────────────────────────────────────────────────
  if (result) {
    return (
      <div>
        <div className="page-header">
          <div>
            <h1 className="page-header-title">Tenant Created</h1>
            <p className="page-header-desc">New organisation registered on the platform</p>
          </div>
        </div>

        <div className="card-enterprise" style={{ maxWidth: "560px" }}>
          <div className="card-enterprise-header">
            <h3>Admin Credentials</h3>
          </div>
          <div className="card-enterprise-body">
            <div className="alert-enterprise alert-enterprise-warning" style={{ marginBottom: "20px" }}>
              Copy the credentials below now. The password <strong>will not be shown again</strong>.
            </div>

            <table className="table-enterprise table-enterprise-compact">
              <tbody>
                <tr>
                  <td className="table-enterprise-label">Tenant Name</td>
                  <td className="table-enterprise-name">{result.tenantName}</td>
                </tr>
                <tr>
                  <td className="table-enterprise-label">Category</td>
                  <td>{result.tenantCategory}</td>
                </tr>
                <tr>
                  <td className="table-enterprise-label">Admin Email</td>
                  <td><code style={{ fontSize: "13px", color: "#2c3e50" }}>{result.adminEmail}</code></td>
                </tr>
                <tr>
                  <td className="table-enterprise-label">Admin Password</td>
                  <td>
                    <code style={{ fontSize: "13px", fontWeight: 700, color: "#c0392b" }}>
                      {result.adminPassword}
                    </code>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div className="card-enterprise-footer">
            <button onClick={onBack} className="btn-enterprise btn-enterprise-primary btn-enterprise-sm">
              ← Back to Tenant List
            </button>
          </div>
        </div>
      </div>
    );
  }

  // ── Form ──────────────────────────────────────────────────────────────────
  return (
    <div>
      <div className="page-header">
        <div className="page-header-left">
          <button onClick={onBack} className="page-header-back">
            ← Back
          </button>
          <div>
            <h1 className="page-header-title">New Tenant</h1>
            <p className="page-header-desc">Register a new organisation and its admin account</p>
          </div>
        </div>
      </div>

      {error && (
        <div className="alert-enterprise alert-enterprise-danger" style={{ maxWidth: "600px", marginBottom: "20px" }}>
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} style={{ maxWidth: "600px" }}>
        {/* Tenant Details */}
        <div className="card-enterprise" style={{ marginBottom: "20px" }}>
          <div className="card-enterprise-header"><h3>Tenant Details</h3></div>
          <div className="card-enterprise-body">
            <div className="form-group-enterprise">
              <label className="form-label-enterprise">Organisation Name</label>
              <input
                name="tenantName"
                value={form.tenantName}
                onChange={handleChange}
                placeholder="e.g. Acme Corporation"
                className="form-control-enterprise"
                required
              />
            </div>
            <div className="form-group-enterprise" style={{ marginBottom: 0 }}>
              <label className="form-label-enterprise">Category</label>
              <select
                name="tenantCategory"
                value={form.tenantCategory}
                onChange={handleChange}
                className="form-control-enterprise"
                required
              >
                <option value="">Select a category…</option>
                <option value="CORPORATE">Corporate</option>
                <option value="EDUCATION">Education</option>
                <option value="TRAINING">Training</option>
              </select>
            </div>
          </div>
        </div>

        {/* Admin Details */}
        <div className="card-enterprise" style={{ marginBottom: "24px" }}>
          <div className="card-enterprise-header"><h3>Admin Account</h3></div>
          <div className="card-enterprise-body">
            <div
              className="alert-enterprise alert-enterprise-light"
              style={{ marginBottom: "20px", fontSize: "12px" }}
            >
              A secure password will be auto-generated and displayed once after creation.
            </div>
            <div className="form-group-enterprise">
              <label className="form-label-enterprise">Full Name</label>
              <input
                name="adminFullName"
                value={form.adminFullName}
                onChange={handleChange}
                placeholder="e.g. Jane Smith"
                className="form-control-enterprise"
                required
              />
            </div>
            <div className="form-group-enterprise" style={{ marginBottom: 0 }}>
              <label className="form-label-enterprise">Email Address</label>
              <input
                name="adminEmail"
                value={form.adminEmail}
                onChange={handleChange}
                placeholder="e.g. admin@acme.com"
                type="email"
                className="form-control-enterprise"
                required
              />
            </div>
          </div>
          <div className="card-enterprise-footer" style={{ display: "flex", gap: "8px" }}>
            <button type="submit" className="btn-enterprise btn-enterprise-primary btn-enterprise-sm" disabled={loading}>
              {loading ? "Creating…" : "Create Tenant"}
            </button>
            <button type="button" className="btn-enterprise btn-enterprise-secondary btn-enterprise-sm" onClick={onBack}>
              Cancel
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}

export default CreateTenantForm;
