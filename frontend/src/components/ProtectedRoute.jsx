import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const ProtectedRoute = ({ children, allowedRoles = [] }) => {
  const { user, isAuthenticated, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <div className="vh-100 d-flex align-items-center justify-content-center">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (allowedRoles.length > 0 && !allowedRoles.includes(user?.role)) {
    const paths = { SUPER_ADMIN: "/superadmin", ADMIN: "/admin", INSTRUCTOR: "/instructor", LEARNER: "/learner" };
    return <Navigate to={paths[user?.role] || "/login"} replace />;
  }

  return children;
};

export const PublicRoute = ({ children }) => {
  const { user, isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="vh-100 d-flex align-items-center justify-content-center">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (isAuthenticated && user) {
    const paths = { SUPER_ADMIN: "/superadmin", ADMIN: "/admin", INSTRUCTOR: "/instructor", LEARNER: "/learner" };
    return <Navigate to={paths[user.role] || "/learner"} replace />;
  }

  return children;
};

export default ProtectedRoute;
