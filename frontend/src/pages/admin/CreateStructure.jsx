import { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { createOrgStructure } from '../../api/orgService';

const emptyType = { name: '', parentName: '' };

export default function CreateStructure() {
  const { user } = useAuth();
  const [structureName, setStructureName] = useState('');
  const [types, setTypes] = useState([{ ...emptyType }]);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  function addType() {
    setTypes((prev) => [...prev, { ...emptyType }]);
  }

  function removeType(idx) {
    setTypes((prev) => prev.filter((_, i) => i !== idx));
  }

  function updateType(idx, field, value) {
    setTypes((prev) => prev.map((t, i) => i === idx ? { ...t, [field]: value } : t));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setSuccess('');
    if (!structureName.trim()) { setError('Structure name is required.'); return; }
    if (types.some(t => !t.name.trim())) { setError('All type levels must have a name.'); return; }
    setLoading(true);
    try {
      await createOrgStructure(user.tenantId, {
        name: structureName,
        orgUnitTypes: types.map(t => ({
          name: t.name.trim(),
          parentName: t.parentName.trim() || null,
        })),
      });
      setSuccess('Org structure created successfully!');
      setStructureName('');
      setTypes([{ ...emptyType }]);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create org structure.');
    } finally {
      setLoading(false);
    }
  }

  const cardStyle = { background: '#fff', borderRadius: 8, padding: 24, boxShadow: '0 1px 4px rgba(0,0,0,0.06)', marginBottom: 20 };
  const inputStyle = { padding: '8px 12px', border: '1px solid #d1d5db', borderRadius: 6, fontSize: 14, width: '100%', boxSizing: 'border-box' };
  const btnStyle = { padding: '8px 18px', background: '#3b82f6', color: '#fff', border: 'none', borderRadius: 6, fontSize: 14, fontWeight: 600, cursor: 'pointer' };

  return (
    <div>
      <h2 style={{ margin: '0 0 24px', color: '#1e293b', fontSize: '22px' }}>Create Org Structure</h2>
      <form onSubmit={handleSubmit}>
        <div style={cardStyle}>
          <div style={{ marginBottom: 18 }}>
            <label style={{ fontWeight: 600, fontSize: 14, color: '#374151', display: 'block', marginBottom: 6 }}>Structure Name</label>
            <input style={inputStyle} value={structureName} onChange={e => setStructureName(e.target.value)} placeholder="e.g. Company Hierarchy" required />
          </div>
        </div>

        <div style={cardStyle}>
          <h3 style={{ margin: '0 0 16px', fontSize: 16, color: '#475569' }}>Org Unit Types (Levels)</h3>
          {types.map((t, idx) => (
            <div key={idx} style={{ display: 'flex', gap: 12, alignItems: 'flex-end', marginBottom: 12 }}>
              <div style={{ flex: 1 }}>
                <label style={{ fontSize: 13, color: '#374151', fontWeight: 600 }}>Type Name</label>
                <input style={inputStyle} value={t.name} onChange={e => updateType(idx, 'name', e.target.value)} placeholder="e.g. Department" />
              </div>
              <div style={{ flex: 1 }}>
                <label style={{ fontSize: 13, color: '#374151', fontWeight: 600 }}>Parent Type Name (optional)</label>
                <input style={inputStyle} value={t.parentName} onChange={e => updateType(idx, 'parentName', e.target.value)} placeholder="e.g. Division" />
              </div>
              <button type="button" onClick={() => removeType(idx)} disabled={types.length === 1}
                style={{ padding: '8px 14px', background: '#fee2e2', color: '#dc2626', border: 'none', borderRadius: 6, cursor: types.length === 1 ? 'not-allowed' : 'pointer', fontWeight: 600 }}>
                ✕
              </button>
            </div>
          ))}
          <button type="button" onClick={addType}
            style={{ ...btnStyle, background: '#f1f5f9', color: '#475569', marginTop: 4 }}>
            + Add Level
          </button>
        </div>

        {error && <div style={{ background: '#fef2f2', border: '1px solid #fecaca', color: '#dc2626', padding: '10px 14px', borderRadius: 6, marginBottom: 16, fontSize: 14 }}>{error}</div>}
        {success && <div style={{ background: '#f0fdf4', border: '1px solid #bbf7d0', color: '#16a34a', padding: '10px 14px', borderRadius: 6, marginBottom: 16, fontSize: 14 }}>{success}</div>}

        <button type="submit" style={{ ...btnStyle, fontSize: 15, padding: '10px 28px' }} disabled={loading}>
          {loading ? 'Creating...' : 'Create Structure'}
        </button>
      </form>
    </div>
  );
}
