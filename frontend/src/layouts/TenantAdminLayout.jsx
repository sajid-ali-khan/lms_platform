import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './TenantAdminLayout.css';

export default function TenantAdminLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <div className="admin-layout">
      <aside className="sidebar">
        <div className="sidebar-header">
          <span className="sidebar-logo">LMS</span>
          <span className="sidebar-title">Admin Panel</span>
        </div>
        <nav className="sidebar-nav">
          <NavLink to="/admin/dashboard" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
            Dashboard
          </NavLink>
          <NavLink to="/admin/organization" end className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
            Organization
          </NavLink>
          <NavLink to="/admin/organization/create" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
            Create Structure
          </NavLink>
          <NavLink to="/admin/organization/update" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
            Add Units
          </NavLink>
          <NavLink to="/admin/users" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
            Users
          </NavLink>
        </nav>
        <div className="sidebar-footer">
          <span className="sidebar-user">{user?.role}</span>
          <button className="logout-btn" onClick={handleLogout}>Logout</button>
        </div>
      </aside>
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
