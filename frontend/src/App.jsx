import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import PublicRoute from './components/PublicRoute';
import TenantAdminLayout from './layouts/TenantAdminLayout';
import Login from './pages/Login';
import TenantDashboard from './pages/admin/TenantDashboard';
import OrgStructure from './pages/admin/OrgStructure';
import CreateStructure from './pages/admin/CreateStructure';
import AddStructure from './pages/admin/AddStructure';
import UserManagement from './pages/admin/UserManagement';
import './App.css';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public routes */}
          <Route path="/" element={<PublicRoute><Navigate to="/login" replace /></PublicRoute>} />
          <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />

          {/* Admin protected routes */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute role="ADMIN">
                <TenantAdminLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="/admin/dashboard" replace />} />
            <Route path="dashboard" element={<TenantDashboard />} />
            <Route path="organization" element={<OrgStructure />} />
            <Route path="organization/create" element={<CreateStructure />} />
            <Route path="organization/update" element={<AddStructure />} />
            <Route path="users" element={<UserManagement />} />
          </Route>

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
