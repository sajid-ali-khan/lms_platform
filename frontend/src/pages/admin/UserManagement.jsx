import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import axiosInstance from '../../api/axiosInstance';

export default function UserManagement() {
  const { user } = useAuth();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [newUser, setNewUser] = useState({ username: '', password: '', role: 'USER' });
  const [creating, setCreating] = useState(false);
  const [createError, setCreateError] = useState('');
  const [createSuccess, setCreateSuccess] = useState('');

  function fetchUsers() {
    if (!user?.tenantId) return;
    setLoading(true);
    axiosInstance.get(`/api/tenants/${user.tenantId}/users`)
      .then((res) => setUsers(res.data || []))
      .catch(() => setError('Failed to load users.'))
      .finally(() => setLoading(false));
  }

  useEffect(() => { fetchUsers(); }, [user]);

  async function handleCreate(e) {
    e.preventDefault();
    setCreateError('');
    setCreateSuccess('');
    setCreating(true);
    try {
      await axiosInstance.post(`/api/tenants/${user.tenantId}/users`, newUser);
      setCreateSuccess('User created successfully!');
      setNewUser({ username: '', password: '', role: 'USER' });
      fetchUsers();
    } catch (err) {
      setCreateError(err.response?.data?.message || 'Failed to create user.');
    } finally {
      setCreating(false);
    }
  }

  const cardStyle = { background: '#fff', borderRadius: 8, padding: 24, boxShadow: '0 1px 4px rgba(0,0,0,0.06)', marginBottom: 20 };
  const inputStyle = { padding: '8px 12px', border: '1px solid #d1d5db', borderRadius: 6, fontSize: 14, width: '100%', boxSizing: 'border-box' };

  return (
    <div>
      <h2 style={{ margin: '0 0 24px', color: '#1e293b', fontSize: '22px' }}>User Management</h2>

      <div style={cardStyle}>
        <h3 style={{ margin: '0 0 16px', fontSize: 16, color: '#475569' }}>Create User</h3>
        {createError && <div style={{ background: '#fef2f2', border: '1px solid #fecaca', color: '#dc2626', padding: '10px', borderRadius: 6, marginBottom: 12, fontSize: 14 }}>{createError}</div>}
        {createSuccess && <div style={{ background: '#f0fdf4', border: '1px solid #bbf7d0', color: '#16a34a', padding: '10px', borderRadius: 6, marginBottom: 12, fontSize: 14 }}>{createSuccess}</div>}
        <form onSubmit={handleCreate} style={{ display: 'flex', gap: 12, flexWrap: 'wrap', alignItems: 'flex-end' }}>
          <div style={{ flex: 1, minWidth: 160 }}>
            <label style={{ fontSize: 13, fontWeight: 600, color: '#374151', display: 'block', marginBottom: 4 }}>Username</label>
            <input style={inputStyle} value={newUser.username} onChange={e => setNewUser(u => ({ ...u, username: e.target.value }))} placeholder="username" required />
          </div>
          <div style={{ flex: 1, minWidth: 160 }}>
            <label style={{ fontSize: 13, fontWeight: 600, color: '#374151', display: 'block', marginBottom: 4 }}>Password</label>
            <input style={inputStyle} type="password" value={newUser.password} onChange={e => setNewUser(u => ({ ...u, password: e.target.value }))} placeholder="password" required />
          </div>
          <div style={{ flex: 1, minWidth: 120 }}>
            <label style={{ fontSize: 13, fontWeight: 600, color: '#374151', display: 'block', marginBottom: 4 }}>Role</label>
            <select style={inputStyle} value={newUser.role} onChange={e => setNewUser(u => ({ ...u, role: e.target.value }))}>
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
              <option value="INSTRUCTOR">INSTRUCTOR</option>
            </select>
          </div>
          <button type="submit" disabled={creating}
            style={{ padding: '9px 22px', background: '#3b82f6', color: '#fff', border: 'none', borderRadius: 6, fontSize: 14, fontWeight: 600, cursor: 'pointer' }}>
            {creating ? 'Creating...' : 'Create'}
          </button>
        </form>
      </div>

      <div style={cardStyle}>
        <h3 style={{ margin: '0 0 16px', fontSize: 16, color: '#475569' }}>Users</h3>
        {loading && <p style={{ color: '#64748b' }}>Loading...</p>}
        {error && <p style={{ color: '#dc2626' }}>{error}</p>}
        {!loading && !error && users.length === 0 && <p style={{ color: '#94a3b8' }}>No users found.</p>}
        {!loading && users.length > 0 && (
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 14 }}>
            <thead>
              <tr style={{ background: '#f8fafc' }}>
                {['ID', 'Username', 'Role', 'Status'].map(h => (
                  <th key={h} style={{ textAlign: 'left', padding: '8px 12px', color: '#475569', fontWeight: 600, fontSize: 12, textTransform: 'uppercase', borderBottom: '1px solid #e2e8f0' }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id} style={{ borderBottom: '1px solid #f1f5f9' }}>
                  <td style={{ padding: '9px 12px', color: '#94a3b8', fontSize: 12 }}>{u.id}</td>
                  <td style={{ padding: '9px 12px', fontWeight: 600, color: '#1e293b' }}>{u.username}</td>
                  <td style={{ padding: '9px 12px' }}>
                    <span style={{ background: '#dbeafe', color: '#1d4ed8', padding: '2px 8px', borderRadius: 20, fontSize: 12, fontWeight: 600 }}>{u.role}</span>
                  </td>
                  <td style={{ padding: '9px 12px', color: u.active !== false ? '#16a34a' : '#dc2626', fontSize: 13 }}>
                    {u.active !== false ? 'Active' : 'Inactive'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
