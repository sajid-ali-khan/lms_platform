import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { getOrgStructuresDetailed, getOrgUnitsTree } from '../../api/orgService';

function TreeNode({ node, depth = 0 }) {
  return (
    <div style={{ marginLeft: depth * 20 }}>
      <div style={{
        padding: '6px 10px',
        margin: '4px 0',
        background: depth === 0 ? '#eff6ff' : '#f8fafc',
        borderLeft: `3px solid ${depth === 0 ? '#3b82f6' : '#cbd5e1'}`,
        borderRadius: 4,
        fontSize: 14,
        color: '#1e293b',
      }}>
        <span style={{ fontWeight: depth === 0 ? 600 : 400 }}>{node.name}</span>
        {node.typeName && <span style={{ marginLeft: 8, fontSize: 12, color: '#64748b' }}>({node.typeName})</span>}
      </div>
      {node.children?.map((child) => (
        <TreeNode key={child.id} node={child} depth={depth + 1} />
      ))}
    </div>
  );
}

export default function OrgStructure() {
  const { user } = useAuth();
  const [structures, setStructures] = useState([]);
  const [selected, setSelected] = useState('');
  const [tree, setTree] = useState(null);
  const [loading, setLoading] = useState(true);
  const [treeLoading, setTreeLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!user?.tenantId) return;
    getOrgStructuresDetailed(user.tenantId)
      .then((res) => {
        setStructures(res.data || []);
        if (res.data?.length > 0) setSelected(String(res.data[0].id));
      })
      .catch(() => setError('Failed to load org structures.'))
      .finally(() => setLoading(false));
  }, [user]);

  useEffect(() => {
    if (!selected || !user?.tenantId) return;
    setTreeLoading(true);
    setTree(null);
    getOrgUnitsTree(user.tenantId, selected)
      .then((res) => setTree(res.data))
      .catch(() => setTree(null))
      .finally(() => setTreeLoading(false));
  }, [selected, user]);

  return (
    <div>
      <h2 style={{ margin: '0 0 24px', color: '#1e293b', fontSize: '22px' }}>Organization Structure</h2>
      {loading && <p style={{ color: '#64748b' }}>Loading...</p>}
      {error && <p style={{ color: '#dc2626' }}>{error}</p>}
      {!loading && !error && (
        <>
          {structures.length === 0 ? (
            <p style={{ color: '#64748b' }}>No structures found.</p>
          ) : (
            <>
              <div style={{ marginBottom: 20 }}>
                <label style={{ fontWeight: 600, fontSize: 14, color: '#374151', marginRight: 10 }}>Select Structure:</label>
                <select
                  value={selected}
                  onChange={(e) => setSelected(e.target.value)}
                  style={{ padding: '7px 12px', borderRadius: 6, border: '1px solid #d1d5db', fontSize: 14 }}
                >
                  {structures.map((s) => (
                    <option key={s.id} value={String(s.id)}>{s.name}</option>
                  ))}
                </select>
              </div>
              <div style={{ background: '#fff', borderRadius: 8, padding: 24, boxShadow: '0 1px 4px rgba(0,0,0,0.06)' }}>
                <h3 style={{ margin: '0 0 16px', fontSize: 15, color: '#475569' }}>Unit Tree</h3>
                {treeLoading && <p style={{ color: '#64748b' }}>Loading tree...</p>}
                {!treeLoading && !tree && <p style={{ color: '#94a3b8', fontSize: 14 }}>No units found for this structure.</p>}
                {!treeLoading && Array.isArray(tree) && tree.map((node) => (
                  <TreeNode key={node.id} node={node} />
                ))}
                {!treeLoading && tree && !Array.isArray(tree) && <TreeNode node={tree} />}
              </div>
            </>
          )}
        </>
      )}
    </div>
  );
}
