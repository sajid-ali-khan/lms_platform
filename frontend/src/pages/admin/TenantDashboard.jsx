import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { getOrgStructuresDetailed } from '../../api/orgService';

export default function TenantDashboard() {
  const { user } = useAuth();
  const [structures, setStructures] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!user?.tenantId) return;
    getOrgStructuresDetailed(user.tenantId)
      .then((res) => setStructures(res.data || []))
      .catch(() => setError('Failed to load org structures.'))
      .finally(() => setLoading(false));
  }, [user]);

  return (
    <div>
      <h2 style={{ margin: '0 0 24px', color: '#1e293b', fontSize: '22px' }}>Dashboard</h2>
      {loading && <p style={{ color: '#64748b' }}>Loading...</p>}
      {error && <p style={{ color: '#dc2626' }}>{error}</p>}
      {!loading && !error && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '16px' }}>
          {structures.length === 0 && (
            <div style={{ background: '#fff', borderRadius: 8, padding: '24px', boxShadow: '0 1px 4px rgba(0,0,0,0.06)' }}>
              <p style={{ color: '#64748b', margin: 0 }}>No org structures found. Create one to get started.</p>
            </div>
          )}
          {structures.map((s) => (
            <div key={s.id} style={{ background: '#fff', borderRadius: 8, padding: '24px', boxShadow: '0 1px 4px rgba(0,0,0,0.06)' }}>
              <h3 style={{ margin: '0 0 12px', fontSize: '16px', color: '#1e293b' }}>{s.name}</h3>
              <p style={{ margin: 0, fontSize: '13px', color: '#64748b' }}>
                {s.structure?.length ?? 0} level(s) defined
              </p>
              {s.structure?.map((level) => (
                <div key={level.id} style={{ marginTop: 8, fontSize: '13px', color: '#475569' }}>
                  <span style={{ fontWeight: 600 }}>Level {level.level}:</span> {level.name}
                  {level.parentName && <span style={{ color: '#94a3b8' }}> (parent: {level.parentName})</span>}
                </div>
              ))}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
