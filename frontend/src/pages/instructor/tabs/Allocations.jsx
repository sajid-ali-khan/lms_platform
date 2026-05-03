import { useState, useEffect, useCallback } from "react";
import {
  getCourseAllocations,
  allocateCourse,
  removeCourseAllocation,
  getOrgStructuresDetailed,
  getOrgUnitsTree,
} from "../../../api.js";

export default function Allocations({ courseId, tenantId }) {
  const [allocations, setAllocations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Add allocation form state
  const [orgStructures, setOrgStructures] = useState([]);
  const [selectedStructure, setSelectedStructure] = useState("");
  const [orgUnitsTree, setOrgUnitsTree] = useState([]);
  const [selectedOrgUnit, setSelectedOrgUnit] = useState("");
  const [isMandatory, setIsMandatory] = useState(false);
  const [saving, setSaving] = useState(false);

  const fetchAllocations = useCallback(async () => {
    if (!courseId) return;
    setLoading(true);
    setError(null);
    try {
      const data = await getCourseAllocations(courseId);
      setAllocations(data);
    } catch {
      setError("Failed to load allocations");
    } finally {
      setLoading(false);
    }
  }, [courseId]);

  const fetchStructures = useCallback(async () => {
    if (!tenantId) return;
    try {
      const data = await getOrgStructuresDetailed(tenantId);
      setOrgStructures(data);
    } catch {
      // ignore
    }
  }, [tenantId]);

  useEffect(() => {
    fetchAllocations();
    fetchStructures();
  }, [fetchAllocations, fetchStructures]);

  // Build full breadcrumb path for each org unit
  const buildOrgUnitPaths = (flatTree) => {
    const byId = Object.fromEntries(flatTree.map((u) => [u.id, u]));
    return flatTree.map((u) => {
      const parts = [];
      let cur = u;
      while (cur) {
        parts.unshift(cur.name);
        cur = cur.parentId ? byId[cur.parentId] : null;
      }
      return { ...u, path: parts.join(" / ") };
    });
  };

  const handleStructureChange = async (structureId) => {
    setSelectedStructure(structureId);
    setSelectedOrgUnit("");
    if (!structureId) {
      setOrgUnitsTree([]);
      return;
    }
    try {
      const tree = await getOrgUnitsTree(tenantId, structureId);
      setOrgUnitsTree(buildOrgUnitPaths(tree));
    } catch {
      setError("Failed to load org units");
    }
  };

  const handleAllocate = async () => {
    if (!selectedOrgUnit) return;
    setSaving(true);
    setError(null);
    try {
      const result = await allocateCourse(courseId, {
        orgUnitId: selectedOrgUnit,
        isMandatory,
      });
      setAllocations((prev) => [...prev, result]);
      setSelectedOrgUnit("");
      setIsMandatory(false);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to allocate course");
    } finally {
      setSaving(false);
    }
  };

  const handleRemove = async (orgUnitId) => {
    setError(null);
    try {
      await removeCourseAllocation(courseId, orgUnitId);
      setAllocations((prev) => prev.filter((a) => a.orgUnitId !== orgUnitId));
    } catch (err) {
      setError(err.response?.data?.message || "Failed to remove allocation");
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: "center", padding: "32px", color: "var(--enterprise-muted)" }}>
        Loading allocations…
      </div>
    );
  }

  return (
    <div>
      <p style={{ color: "var(--enterprise-muted)", fontSize: "0.875rem", marginBottom: "1.25rem" }}>
        Allocate this course to org units so that learners in those units can see and enroll in it.
      </p>

      {error && (
        <div className="alert-enterprise alert-enterprise-danger" style={{ marginBottom: 16 }}>
          {error}
        </div>
      )}

      {/* Current allocations */}
      <div style={{ marginBottom: "1.5rem" }}>
        <h4 style={{ fontSize: "0.95rem", fontWeight: 600, marginBottom: 12, color: "var(--enterprise-text)" }}>
          Current Allocations
        </h4>
        {allocations.length === 0 ? (
          <div style={{ color: "var(--enterprise-muted)", fontSize: "0.875rem", padding: "12px 0" }}>
            No allocations yet. This course is not visible to any org-unit-filtered learners.
          </div>
        ) : (
          <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>
            {allocations.map((a) => (
              <div
                key={a.orgUnitId}
                style={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between",
                  padding: "10px 14px",
                  background: "#f8f9fa",
                  borderRadius: "6px",
                  border: "1px solid var(--enterprise-border)",
                }}
              >
                <div>
                  <span style={{ fontWeight: 500 }}>{a.orgUnitPath || a.orgUnitName}</span>
                  {a.isMandatory && (
                    <span
                      className="badge-enterprise"
                      style={{ marginLeft: 8, background: "#fff3cd", color: "#856404", fontSize: "0.75rem" }}
                    >
                      Mandatory
                    </span>
                  )}
                </div>
                <button
                  className="btn-enterprise-ghost"
                  style={{ color: "#dc3545", fontSize: "0.8rem", padding: "4px 10px" }}
                  onClick={() => handleRemove(a.orgUnitId)}
                >
                  Remove
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Add allocation form */}
      <div style={{ borderTop: "1px solid var(--enterprise-border)", paddingTop: "1.25rem" }}>
        <h4 style={{ fontSize: "0.95rem", fontWeight: 600, marginBottom: 12, color: "var(--enterprise-text)" }}>
          Add Allocation
        </h4>

        <div className="form-group-enterprise">
          <label className="form-label-enterprise">Org Structure</label>
          <select
            className="form-control-enterprise"
            value={selectedStructure}
            onChange={(e) => handleStructureChange(e.target.value)}
          >
            <option value="">Select a structure…</option>
            {orgStructures.map((s) => (
              <option key={s.id} value={s.id}>{s.name}</option>
            ))}
          </select>
        </div>

        {orgUnitsTree.length > 0 && (
          <div className="form-group-enterprise">
            <label className="form-label-enterprise">Org Unit</label>
            <select
              className="form-control-enterprise"
              value={selectedOrgUnit}
              onChange={(e) => setSelectedOrgUnit(e.target.value)}
            >
              <option value="">Select an org unit…</option>
              {orgUnitsTree.map((ou) => (
                <option key={ou.id} value={ou.id}>
                  {ou.path}
                </option>
              ))}
            </select>
          </div>
        )}

        {selectedOrgUnit && (
          <div style={{ display: "flex", alignItems: "center", gap: "16px", marginTop: 8 }}>
            <label style={{ display: "flex", alignItems: "center", gap: 6, fontSize: "0.875rem", cursor: "pointer" }}>
              <input
                type="checkbox"
                checked={isMandatory}
                onChange={(e) => setIsMandatory(e.target.checked)}
              />
              Mandatory
            </label>
            <button
              className="btn-enterprise-primary"
              onClick={handleAllocate}
              disabled={saving}
            >
              {saving ? "Allocating…" : "Allocate"}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
