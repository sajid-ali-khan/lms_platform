import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { getOrgStructuresDetailed, getOrgUnitsTree, createOrgUnit } from "../../api";

export default function AddStructure() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const tenantId = user?.tenantId;

  // available structures (detailed)
  const [structures, setStructures] = useState([]);
  const [loadingStructures, setLoadingStructures] = useState(true);
  const [selectedStructureId, setSelectedStructureId] = useState("");

  // existing units for the selected structure (flat)
  const [flatUnits, setFlatUnits] = useState([]);
  const [loadingUnits, setLoadingUnits] = useState(false);

  // per-level form state: { [typeId]: { name, parentOrgUnitId } }
  const [levelInputs, setLevelInputs] = useState({});
  const [savingLevel, setSavingLevel] = useState(null);
  const [levelError, setLevelError] = useState({});

  // global update
  const [refreshing, setRefreshing] = useState(false);
  const [updateSuccess, setUpdateSuccess] = useState(false);

  // Load structures
  useEffect(() => {
    if (!tenantId) return;
    getOrgStructuresDetailed(tenantId)
      .then((data) => {
        setStructures(data);
        if (data.length > 0) setSelectedStructureId(String(data[0].id));
      })
      .catch(() => {})
      .finally(() => setLoadingStructures(false));
  }, [tenantId]);

  // Fetch units when structure changes
  const refreshUnits = (structId) => {
    if (!structId || !tenantId) return;
    setLoadingUnits(true);
    getOrgUnitsTree(tenantId, structId)
      .then(setFlatUnits)
      .catch(() => setFlatUnits([]))
      .finally(() => setLoadingUnits(false));
  };

  useEffect(() => { refreshUnits(selectedStructureId); }, [selectedStructureId]);

  const handleUpdateStructure = async () => {
    setRefreshing(true);
    setUpdateSuccess(false);
    try {
      await new Promise((resolve) => {
        setLoadingUnits(true);
        getOrgUnitsTree(tenantId, selectedStructureId)
          .then((data) => { setFlatUnits(data); resolve(); })
          .catch(() => { setFlatUnits([]); resolve(); })
          .finally(() => setLoadingUnits(false));
      });
      setUpdateSuccess(true);
      setTimeout(() => setUpdateSuccess(false), 3000);
    } finally {
      setRefreshing(false);
    }
  };

  const selectedStructure = structures.find((s) => String(s.id) === selectedStructureId);

  // Sorted unit type levels
  const sortedTypes = selectedStructure
    ? [...selectedStructure.structure].sort((a, b) => a.level - b.level)
    : [];

  // Units grouped by level
  const unitsByLevel = {};
  flatUnits.forEach((u) => {
    if (!unitsByLevel[u.level]) unitsByLevel[u.level] = [];
    unitsByLevel[u.level].push(u);
  });

  // levelInputs shape: { [typeId]: { name: string, ancestors: { [level]: unitId } } }
  const getName = (typeId) => levelInputs[typeId]?.name || "";
  const setName = (typeId, value) =>
    setLevelInputs((prev) => ({ ...prev, [typeId]: { ...prev[typeId], name: value } }));
  const getAncestor = (typeId, level) => levelInputs[typeId]?.ancestors?.[level] || "";
  const setAncestor = (typeId, level, value) =>
    setLevelInputs((prev) => ({
      ...prev,
      [typeId]: {
        ...prev[typeId],
        ancestors: {
          // clear all selections below the changed level
          ...Object.fromEntries(
            Object.entries(prev[typeId]?.ancestors || {}).filter(([l]) => Number(l) < level)
          ),
          [level]: value,
        },
      },
    }));

  // Returns units at `level` whose parentId matches the selected ancestor at level-1
  const getFilteredUnits = (typeId, level) => {
    const units = unitsByLevel[level] || [];
    if (level === 0) return units;
    const parentId = getAncestor(typeId, level - 1);
    if (!parentId) return [];
    return units.filter((u) => String(u.parentId) === String(parentId));
  };

  const handleAdd = async (type) => {
    const name = getName(type.id).trim();
    // The direct parent is the ancestor at level N-1
    const parentOrgUnitId = type.level > 0 ? getAncestor(type.id, type.level - 1) || null : null;

    if (!name) {
      setLevelError((prev) => ({ ...prev, [type.id]: "Name is required." }));
      return;
    }
    if (type.level > 0 && !parentOrgUnitId) {
      setLevelError((prev) => ({ ...prev, [type.id]: `Select all parent levels first.` }));
      return;
    }

    setSavingLevel(type.id);
    setLevelError((prev) => ({ ...prev, [type.id]: "" }));
    try {
      await createOrgUnit(tenantId, {
        orgUnitTypeId: type.id,
        name,
        parentOrgUnitId: parentOrgUnitId || null,
        attributes: {},
      });
      setLevelInputs((prev) => ({ ...prev, [type.id]: { name: "", ancestors: {} } }));
      refreshUnits(selectedStructureId);
    } catch {
      setLevelError((prev) => ({ ...prev, [type.id]: "Failed to add unit. Try again." }));
    } finally {
      setSavingLevel(null);
    }
  };

  if (loadingStructures) {
    return (
      <div className="d-flex align-items-center" style={{ color: "var(--enterprise-muted)", gap: "0.5rem" }}>
        <div className="loading-enterprise"></div> Loading structures…
      </div>
    );
  }

  if (structures.length === 0) {
    return (
      <div className="empty-state-enterprise">
        <p>No organization structures found.</p>
        <button onClick={() => navigate("/admin/organization/create")} className="btn-enterprise-primary">
          Create a structure first
        </button>
      </div>
    );
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-header-title">Add Organization Units</h1>
          <p style={{ color: "var(--enterprise-muted)", marginBottom: 0 }}>Add units level by level. Each addition is saved immediately.</p>
        </div>
        <button onClick={() => navigate("/admin/organization")} className="btn-enterprise-ghost">
          ← Back
        </button>
      </div>

      {/* Structure selector */}
      <div className="card-enterprise mb-4">
        <div className="card-enterprise-body d-flex align-items-center gap-3">
          <label className="form-label-enterprise mb-0" style={{ whiteSpace: "nowrap" }}>Structure:</label>
          <select
            className="form-control-enterprise"
            style={{ maxWidth: "280px" }}
            value={selectedStructureId}
            onChange={(e) => { setSelectedStructureId(e.target.value); setLevelInputs({}); }}
          >
            {structures.map((s) => (
              <option key={s.id} value={String(s.id)}>{s.name}</option>
            ))}
          </select>
          {sortedTypes.length > 0 && (
            <span style={{ fontSize: "0.8rem", color: "var(--enterprise-muted)" }}>
              {sortedTypes.map((t) => t.name).join(" → ")}
            </span>
          )}
        </div>
      </div>

      {/* Global Update bar */}
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          padding: "0.75rem 1.25rem",
          marginBottom: "1.25rem",
          background: updateSuccess ? "#d4edda" : "var(--enterprise-bg)",
          border: `1px solid ${updateSuccess ? "#c3e6cb" : "var(--enterprise-border)"}`,
          borderRadius: "6px",
          transition: "background 0.3s, border-color 0.3s",
        }}
      >
        <div style={{ fontSize: "0.875rem", color: updateSuccess ? "#155724" : "var(--enterprise-muted)" }}>
          {updateSuccess
            ? `✓ Structure updated — ${flatUnits.length} unit${flatUnits.length !== 1 ? "s" : ""} across ${sortedTypes.length} level${sortedTypes.length !== 1 ? "s" : ""}`
            : flatUnits.length === 0
            ? "No units added yet. Start with Level 0 below."
            : `${flatUnits.length} unit${flatUnits.length !== 1 ? "s" : ""} across ${sortedTypes.length} level${sortedTypes.length !== 1 ? "s" : ""}. Add more below, then click Update.`}
        </div>
        <button
          onClick={handleUpdateStructure}
          className="btn-enterprise-primary"
          disabled={refreshing}
          style={{ whiteSpace: "nowrap", minWidth: "160px" }}
        >
          {refreshing ? (
            <span style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
              <span style={{ width: "14px", height: "14px", border: "2px solid rgba(255,255,255,0.4)", borderTopColor: "#fff", borderRadius: "50%", display: "inline-block", animation: "spin 0.7s linear infinite" }} />
              Updating…
            </span>
          ) : "↻ Update Structure"}
        </button>
      </div>

      {loadingUnits ? (
        <div className="d-flex align-items-center" style={{ color: "var(--enterprise-muted)", gap: "0.5rem" }}>
          <div className="loading-enterprise"></div> Loading units…
        </div>
      ) : (
        <div className="row g-3">
          {sortedTypes.map((type) => {
            const existingUnits = unitsByLevel[type.level] || [];
            const isSaving = savingLevel === type.id;
            const err = levelError[type.id];
            // ancestor levels above this type (0 .. type.level-1)
            const ancestorLevels = sortedTypes.filter((t) => t.level < type.level);

            const directParentId = type.level > 0 ? getAncestor(type.id, type.level - 1) : null;
            const shownUnits = type.level === 0
              ? existingUnits
              : directParentId
                ? existingUnits.filter((u) => String(u.parentId) === String(directParentId))
                : [];

            return (
              <div key={type.id} className="col-md-4">
                <div className="card-enterprise" style={{ height: "100%" }}>
                  <div className="card-enterprise-header">
                    <span style={{ fontWeight: 600 }}>Level {type.level}: {type.name}</span>
                    <span
                      style={{
                        fontSize: "0.72rem",
                        background: "var(--enterprise-bg)",
                        border: "1px solid var(--enterprise-border)",
                        borderRadius: "12px",
                        padding: "1px 8px",
                        color: "var(--enterprise-muted)",
                      }}
                    >
                      {shownUnits.length} unit{shownUnits.length !== 1 ? "s" : ""}
                    </span>
                  </div>
                  <div className="card-enterprise-body">
                    {/* Add form */}
                    {err && <div className="alert-enterprise-danger mb-2" style={{ padding: "0.4rem 0.75rem", fontSize: "0.8rem" }}>{err}</div>}

                    {/* Cascading ancestor selectors */}
                    {ancestorLevels.map((ancestorType) => {
                      const options = getFilteredUnits(type.id, ancestorType.level);
                      const isDisabled = isSaving || (ancestorType.level > 0 && !getAncestor(type.id, ancestorType.level - 1));
                      return (
                        <div key={ancestorType.id} className="form-group-enterprise">
                          <label className="form-label-enterprise">{ancestorType.name}</label>
                          <select
                            className="form-control-enterprise"
                            value={getAncestor(type.id, ancestorType.level)}
                            onChange={(e) => setAncestor(type.id, ancestorType.level, e.target.value)}
                            disabled={isDisabled}
                          >
                            <option value="">Select {ancestorType.name}…</option>
                            {options.map((u) => (
                              <option key={u.id} value={u.id}>{u.name}</option>
                            ))}
                          </select>
                        </div>
                      );
                    })}

                    <div className="form-group-enterprise">
                      <label className="form-label-enterprise">{type.name} Name</label>
                      <div className="d-flex gap-2">
                        <input
                          className="form-control-enterprise"
                          placeholder={`e.g. ${type.name} A`}
                          value={getName(type.id)}
                          onChange={(e) => setName(type.id, e.target.value)}
                          onKeyDown={(e) => { if (e.key === "Enter") handleAdd(type); }}
                          disabled={isSaving}
                        />
                        <button
                          onClick={() => handleAdd(type)}
                          className="btn-enterprise-primary"
                          style={{ whiteSpace: "nowrap" }}
                          disabled={isSaving}
                        >
                          {isSaving ? "…" : "+ Add"}
                        </button>
                      </div>
                    </div>

                    {/* Existing units list */}
                    {shownUnits.length > 0 && (
                      <div
                        style={{
                          marginTop: "0.75rem",
                          borderTop: "1px solid var(--enterprise-border)",
                          paddingTop: "0.75rem",
                          maxHeight: "180px",
                          overflowY: "auto",
                        }}
                      >
                        {shownUnits.map((u) => (
                          <div
                            key={u.id}
                            style={{
                              display: "flex",
                              alignItems: "center",
                              gap: "0.5rem",
                              padding: "0.35rem 0",
                              fontSize: "0.875rem",
                              borderBottom: "1px solid var(--enterprise-border)",
                            }}
                          >
                            <span
                              style={{
                                display: "inline-block",
                                width: "6px",
                                height: "6px",
                                borderRadius: "50%",
                                background: "var(--enterprise-primary)",
                                flexShrink: 0,
                              }}
                            />
                            <span>{u.name}</span>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
