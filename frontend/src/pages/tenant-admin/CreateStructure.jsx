import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { createOrgStructure } from "../../api";

export default function CreateStructure() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const tenantId = user?.tenantId;

  const [name, setName] = useState("");
  const [levelInput, setLevelInput] = useState("");
  const [levels, setLevels] = useState([]);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const addLevel = () => {
    const trimmed = levelInput.trim();
    if (!trimmed) return;
    setLevels([...levels, trimmed]);
    setLevelInput("");
  };

  const removeLevel = (idx) => {
    setLevels(levels.filter((_, i) => i !== idx));
  };

  const handleLevelKeyDown = (e) => {
    if (e.key === "Enter") { e.preventDefault(); addLevel(); }
  };

  const handleCreate = async () => {
    setError("");
    if (!name.trim()) { setError("Structure name is required."); return; }
    if (levels.length === 0) { setError("Add at least one hierarchy level."); return; }

    setSaving(true);
    try {
      await createOrgStructure(tenantId, { name: name.trim(), hierarchyLevels: levels });
      setSuccess(`Structure "${name.trim()}" created successfully.`);
      setTimeout(() => navigate("/admin/organization"), 1000);
    } catch {
      setError("Failed to create structure. Please try again.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-header-title">Create Organization Structure</h1>
          <p style={{ color: "var(--enterprise-muted)", marginBottom: 0 }}>Define the structure name and its hierarchy levels.</p>
        </div>
        <button onClick={() => navigate("/admin/organization")} className="btn-enterprise-ghost">
          ← Back
        </button>
      </div>

      <div className="card-enterprise" style={{ maxWidth: "520px" }}>
        <div className="card-enterprise-header">
          <span style={{ fontWeight: 600 }}>Structure Details</span>
        </div>
        <div className="card-enterprise-body">
          {error && <div className="alert-enterprise-danger mb-3">{error}</div>}
          {success && <div className="alert-enterprise-success mb-3">{success}</div>}

          <div className="form-group-enterprise">
            <label className="form-label-enterprise">Structure Name</label>
            <input
              className="form-control-enterprise"
              placeholder="e.g. Academics, Operations"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>

          <div className="form-group-enterprise">
            <label className="form-label-enterprise">Hierarchy Levels</label>
            <p style={{ fontSize: "0.8rem", color: "var(--enterprise-muted)", marginBottom: "0.5rem" }}>
              Add levels from top to bottom (e.g. Branch → Semester → Section)
            </p>

            {levels.length > 0 && (
              <div className="mb-3">
                {levels.map((lvl, i) => (
                  <div
                    key={i}
                    className="d-flex align-items-center justify-content-between"
                    style={{
                      padding: "0.5rem 0.75rem",
                      marginBottom: "0.4rem",
                      background: "var(--enterprise-bg)",
                      border: "1px solid var(--enterprise-border)",
                      borderRadius: "6px",
                      fontSize: "0.875rem",
                    }}
                  >
                    <span>
                      <span style={{ color: "var(--enterprise-muted)", marginRight: "0.5rem" }}>Level {i + 1}</span>
                      <strong>{lvl}</strong>
                    </span>
                    <button
                      type="button"
                      onClick={() => removeLevel(i)}
                      style={{ background: "none", border: "none", color: "var(--enterprise-muted)", cursor: "pointer", padding: "0 4px", lineHeight: 1 }}
                    >
                      ✕
                    </button>
                  </div>
                ))}
              </div>
            )}

            <div className="d-flex gap-2">
              <input
                className="form-control-enterprise"
                placeholder="Level name…"
                value={levelInput}
                onChange={(e) => setLevelInput(e.target.value)}
                onKeyDown={handleLevelKeyDown}
              />
              <button type="button" onClick={addLevel} className="btn-enterprise-ghost" style={{ whiteSpace: "nowrap" }}>
                + Add
              </button>
            </div>
          </div>

          <div
            style={{
              padding: "0.75rem 1rem",
              background: "#fff8e1",
              border: "1px solid #ffe082",
              borderRadius: "6px",
              fontSize: "0.8rem",
              color: "#7c5c00",
              marginBottom: "1.25rem",
            }}
          >
            ⚠ Hierarchy levels cannot be changed after the structure is created.
          </div>

          <div className="d-flex justify-content-between align-items-center">
            <button onClick={() => navigate("/admin/organization")} className="btn-enterprise-ghost" disabled={saving}>
              Cancel
            </button>
            <button onClick={handleCreate} className="btn-enterprise-primary" disabled={saving}>
              {saving ? "Creating…" : "Create Structure"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
