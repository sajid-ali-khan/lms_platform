import { useEffect, useState, useCallback } from 'react';
import { useAuth } from '../../context/AuthContext';
import {
  getOrgStructuresDetailed,
  createOrgUnit,
  getOrgUnitsTree,
  getOrgUnits,
} from '../../api/orgService';
import './AddStructure.css';

function TreeNode({ node, depth = 0 }) {
  return (
    <div className="tree-node" style={{ marginLeft: depth * 20 }}>
      <div className={`tree-item ${depth === 0 ? 'tree-root' : ''}`}>
        <span className="tree-name">{node.name}</span>
        {node.typeName && <span className="tree-type">({node.typeName})</span>}
      </div>
      {node.children?.map((child) => (
        <TreeNode key={child.id} node={child} depth={depth + 1} />
      ))}
    </div>
  );
}

export default function AddStructure() {
  const { user } = useAuth();

  // Org structures list (from detailed endpoint)
  const [structures, setStructures] = useState([]);
  const [selectedStructureId, setSelectedStructureId] = useState('');
  const [structuresLoading, setStructuresLoading] = useState(true);
  const [structuresError, setStructuresError] = useState('');

  // Org unit types for selected structure (levels)
  const [unitTypes, setUnitTypes] = useState([]);

  // Available units per type (for parent selection), keyed by typeId
  const [availableUnits, setAvailableUnits] = useState({});

  // Pending entries: { typeId, typeName, name, parentOrgUnitId, parentOrgUnitName }[]
  const [pendingEntries, setPendingEntries] = useState([]);

  // Per-type form state: { [typeId]: { name: '', parentOrgUnitId: '' } }
  const [formState, setFormState] = useState({});

  // Current org units tree
  const [tree, setTree] = useState(null);
  const [treeLoading, setTreeLoading] = useState(false);

  // Submission state
  const [submitting, setSubmitting] = useState(false);
  const [submitSuccess, setSubmitSuccess] = useState('');
  const [submitError, setSubmitError] = useState('');

  // ----- Load structures on mount -----
  useEffect(() => {
    if (!user?.tenantId) return;
    setStructuresLoading(true);
    getOrgStructuresDetailed(user.tenantId)
      .then((res) => {
        const data = res.data || [];
        setStructures(data);
        if (data.length > 0) setSelectedStructureId(String(data[0].id));
      })
      .catch(() => setStructuresError('Failed to load org structures.'))
      .finally(() => setStructuresLoading(false));
  }, [user]);

  // ----- When structure selection changes, update unit types and reset -----
  useEffect(() => {
    if (!selectedStructureId) {
      setUnitTypes([]);
      setPendingEntries([]);
      setFormState({});
      setAvailableUnits({});
      return;
    }
    const struct = structures.find((s) => String(s.id) === selectedStructureId);
    const levels = struct?.structure || [];
    setUnitTypes(levels);
    setPendingEntries([]);
    setSubmitSuccess('');
    setSubmitError('');
    // Initialize form state for each type
    const fs = {};
    levels.forEach((t) => { fs[t.id] = { name: '', parentOrgUnitId: '' }; });
    setFormState(fs);
    setAvailableUnits({});
  }, [selectedStructureId, structures]);

  // ----- Load existing units for each type (for parent dropdowns) -----
  const refreshUnits = useCallback(() => {
    if (!selectedStructureId || !user?.tenantId || unitTypes.length === 0) return;
    unitTypes.forEach((t) => {
      getOrgUnits(user.tenantId, selectedStructureId, t.id, null)
        .then((res) => {
          setAvailableUnits((prev) => ({ ...prev, [t.id]: res.data || [] }));
        })
        .catch(() => {});
    });
  }, [selectedStructureId, user, unitTypes]);

  useEffect(() => {
    refreshUnits();
  }, [refreshUnits]);

  // ----- Load tree -----
  const refreshTree = useCallback(() => {
    if (!selectedStructureId || !user?.tenantId) return;
    setTreeLoading(true);
    setTree(null);
    getOrgUnitsTree(user.tenantId, selectedStructureId)
      .then((res) => setTree(res.data))
      .catch(() => setTree(null))
      .finally(() => setTreeLoading(false));
  }, [selectedStructureId, user]);

  useEffect(() => {
    refreshTree();
  }, [refreshTree]);

  // ----- Form helpers -----
  function updateForm(typeId, field, value) {
    setFormState((prev) => ({ ...prev, [typeId]: { ...prev[typeId], [field]: value } }));
  }

  function addPendingEntry(typeId, typeName) {
    const { name, parentOrgUnitId } = formState[typeId] || {};
    if (!name?.trim()) return;

    // Find parent name for display
    const parentUnit = (availableUnits[typeId] || []).find(
      (u) => String(u.id) === String(parentOrgUnitId)
    );

    // Also check pending entries as potential parents
    const pendingParent = pendingEntries.find(
      (e) => String(e._tempId) === String(parentOrgUnitId)
    );

    setPendingEntries((prev) => [
      ...prev,
      {
        _tempId: Date.now() + Math.random(),
        typeId,
        typeName,
        name: name.trim(),
        parentOrgUnitId: parentOrgUnitId || null,
        parentOrgUnitName: parentUnit?.name || pendingParent?.name || null,
      },
    ]);
    // Reset only name and parent for this type
    setFormState((prev) => ({ ...prev, [typeId]: { name: '', parentOrgUnitId: '' } }));
  }

  function removePendingEntry(tempId) {
    setPendingEntries((prev) => prev.filter((e) => e._tempId !== tempId));
  }

  // ----- Global submit -----
  async function handleUpdateStructure() {
    if (pendingEntries.length === 0) {
      setSubmitError('No pending entries to submit. Add at least one unit below.');
      return;
    }
    setSubmitting(true);
    setSubmitSuccess('');
    setSubmitError('');

    let successCount = 0;
    const errors = [];

    // We process sequentially so that newly created units can be used as parents
    // for subsequent entries in the same batch.
    const idMap = {}; // tempId -> real id (for same-batch parent resolution)

    for (const entry of pendingEntries) {
      try {
        // Resolve parentOrgUnitId: if it was a tempId, replace with real id
        let resolvedParentId = entry.parentOrgUnitId;
        if (resolvedParentId && idMap[resolvedParentId] !== undefined) {
          resolvedParentId = idMap[resolvedParentId];
        }

        const res = await createOrgUnit(user.tenantId, {
          orgUnitTypeId: entry.typeId,
          name: entry.name,
          parentOrgUnitId: resolvedParentId || null,
          attributes: {},
        });
        idMap[entry._tempId] = res.data?.id;
        successCount++;
      } catch (err) {
        errors.push(`"${entry.name}" (${entry.typeName}): ${err.response?.data?.message || 'Failed'}`);
      }
    }

    setSubmitting(false);

    if (errors.length === 0) {
      setSubmitSuccess(`Successfully added ${successCount} org unit(s).`);
      setPendingEntries([]);
    } else {
      setSubmitError(`${successCount} succeeded. Errors:\n${errors.join('\n')}`);
      // Remove successfully processed entries (keep failed ones)
      const failedNames = errors.map((e) => e.split('"')[1]);
      setPendingEntries((prev) => prev.filter((e) => failedNames.includes(e.name)));
    }

    // Refresh units and tree after submission
    refreshUnits();
    refreshTree();
  }

  // ----- Render -----
  return (
    <div className="add-structure-page">
      <div className="page-header">
        <h2 className="page-title">Add Org Units</h2>
        <p className="page-subtitle">Select a structure, add unit entries per level, then click <strong>Update Structure</strong> to save all at once.</p>
      </div>

      {/* ===== GLOBAL UPDATE BUTTON (TOP) ===== */}
      <div className="global-action-bar top">
        <div className="pending-summary">
          {pendingEntries.length > 0
            ? <span className="pending-badge">{pendingEntries.length} unit{pendingEntries.length !== 1 ? 's' : ''} pending</span>
            : <span className="pending-none">No pending units</span>
          }
        </div>
        <button
          className={`btn-update-structure ${submitting ? 'loading' : ''}`}
          onClick={handleUpdateStructure}
          disabled={submitting || pendingEntries.length === 0}
        >
          {submitting ? (
            <><span className="spinner" /> Updating...</>
          ) : (
            '⬆ Update Structure'
          )}
        </button>
      </div>

      {/* Feedback */}
      {submitSuccess && (
        <div className="feedback success">{submitSuccess}</div>
      )}
      {submitError && (
        <div className="feedback error" style={{ whiteSpace: 'pre-line' }}>{submitError}</div>
      )}

      {/* ===== Structure selector ===== */}
      <div className="card">
        <h3 className="card-title">Select Org Structure</h3>
        {structuresLoading && <p className="muted">Loading structures...</p>}
        {structuresError && <p className="error-text">{structuresError}</p>}
        {!structuresLoading && !structuresError && (
          structures.length === 0 ? (
            <p className="muted">No structures found. Create one first.</p>
          ) : (
            <div className="structure-selector">
              {structures.map((s) => (
                <label key={s.id} className={`structure-option ${String(s.id) === selectedStructureId ? 'selected' : ''}`}>
                  <input
                    type="radio"
                    name="structure"
                    value={String(s.id)}
                    checked={String(s.id) === selectedStructureId}
                    onChange={() => setSelectedStructureId(String(s.id))}
                  />
                  <span className="structure-name">{s.name}</span>
                  <span className="structure-meta">{s.structure?.length ?? 0} level(s)</span>
                </label>
              ))}
            </div>
          )
        )}
      </div>

      {/* ===== Unit type entry panels ===== */}
      {unitTypes.length > 0 && (
        <div className="card">
          <h3 className="card-title">Add Units by Level</h3>
          <div className="type-panels">
            {unitTypes.map((type) => (
              <div key={type.id} className="type-panel">
                <div className="type-panel-header">
                  <span className="type-badge">Level {type.level ?? ''}</span>
                  <span className="type-name">{type.name}</span>
                  {type.parentName && <span className="type-parent">parent: {type.parentName}</span>}
                </div>
                <div className="type-panel-form">
                  <input
                    className="unit-name-input"
                    type="text"
                    placeholder={`${type.name} name`}
                    value={formState[type.id]?.name || ''}
                    onChange={(e) => updateForm(type.id, 'name', e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && (e.preventDefault(), addPendingEntry(type.id, type.name))}
                  />
                  <select
                    className="parent-select"
                    value={formState[type.id]?.parentOrgUnitId || ''}
                    onChange={(e) => updateForm(type.id, 'parentOrgUnitId', e.target.value)}
                  >
                    <option value="">— No parent unit —</option>
                    {/* Existing units as parents */}
                    {(availableUnits[type.id] || []).length > 0 && (
                      <optgroup label="Existing units">
                        {(availableUnits[type.id] || []).map((u) => (
                          <option key={u.id} value={String(u.id)}>{u.name}</option>
                        ))}
                      </optgroup>
                    )}
                    {/* Pending units of the same type as parents */}
                    {pendingEntries.filter((e) => e.typeId === type.id).length > 0 && (
                      <optgroup label="Pending (same level)">
                        {pendingEntries.filter((e) => e.typeId === type.id).map((e) => (
                          <option key={e._tempId} value={String(e._tempId)}>{e.name} (pending)</option>
                        ))}
                      </optgroup>
                    )}
                  </select>
                  <button
                    className="btn-add-entry"
                    type="button"
                    onClick={() => addPendingEntry(type.id, type.name)}
                    disabled={!formState[type.id]?.name?.trim()}
                  >
                    + Add
                  </button>
                </div>
                {/* Pending entries for this type */}
                {pendingEntries.filter((e) => e.typeId === type.id).length > 0 && (
                  <div className="pending-list">
                    {pendingEntries
                      .filter((e) => e.typeId === type.id)
                      .map((e) => (
                        <div key={e._tempId} className="pending-entry">
                          <span className="entry-name">{e.name}</span>
                          {e.parentOrgUnitName && (
                            <span className="entry-parent">↳ {e.parentOrgUnitName}</span>
                          )}
                          <button className="btn-remove" onClick={() => removePendingEntry(e._tempId)}>✕</button>
                        </div>
                      ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ===== Pending entries summary ===== */}
      {pendingEntries.length > 0 && (
        <div className="card pending-card">
          <h3 className="card-title">Pending Entries ({pendingEntries.length})</h3>
          <table className="pending-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Name</th>
                <th>Type (Level)</th>
                <th>Parent Unit</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {pendingEntries.map((e, idx) => (
                <tr key={e._tempId}>
                  <td className="idx-cell">{idx + 1}</td>
                  <td className="name-cell">{e.name}</td>
                  <td className="type-cell">{e.typeName}</td>
                  <td className="parent-cell">{e.parentOrgUnitName || <span className="muted">—</span>}</td>
                  <td><button className="btn-remove-sm" onClick={() => removePendingEntry(e._tempId)}>Remove</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* ===== GLOBAL UPDATE BUTTON (BOTTOM) ===== */}
      <div className="global-action-bar bottom">
        <div className="pending-summary">
          {pendingEntries.length > 0
            ? <span className="pending-badge">{pendingEntries.length} unit{pendingEntries.length !== 1 ? 's' : ''} ready to submit</span>
            : <span className="pending-none">Add units above to enable submission</span>
          }
        </div>
        <button
          className={`btn-update-structure large ${submitting ? 'loading' : ''}`}
          onClick={handleUpdateStructure}
          disabled={submitting || pendingEntries.length === 0}
        >
          {submitting ? (
            <><span className="spinner" /> Updating Structure...</>
          ) : (
            '⬆ Update Structure'
          )}
        </button>
      </div>

      {/* ===== Current tree ===== */}
      {selectedStructureId && (
        <div className="card tree-card">
          <h3 className="card-title">Current Unit Tree</h3>
          {treeLoading && <p className="muted">Loading tree...</p>}
          {!treeLoading && !tree && <p className="muted">No units found for this structure yet.</p>}
          {!treeLoading && Array.isArray(tree) && tree.length === 0 && (
            <p className="muted">No units found for this structure yet.</p>
          )}
          {!treeLoading && tree && (
            <div className="tree-container">
              {Array.isArray(tree)
                ? tree.map((node) => <TreeNode key={node.id} node={node} />)
                : <TreeNode node={tree} />
              }
            </div>
          )}
        </div>
      )}
    </div>
  );
}
