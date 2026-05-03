import { createContext, useContext, useState, useEffect } from "react";
import { login as apiLogin, logout as apiLogout, getUser } from "../api";

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

const getDashboardPath = (role) => {
  const paths = {
    SUPER_ADMIN: "/superadmin",
    ADMIN: "/admin",
    INSTRUCTOR: "/instructor",
    LEARNER: "/learner",
  };
  return paths[role] || "/learner";
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedUser = getUser();
    if (storedUser) setUser(storedUser);
    setLoading(false);
  }, []);

  const login = async (username, password) => {
    try {
      const data = await apiLogin(username, password);
      const userData = {
        id: data.userId,
        email: data.userEmail,
        fullName: data.fullName,
        role: data.role,
        tenantId: data.tenantId,
        tenantName: data.tenantName,
      };
      setUser(userData);
      return { success: true, redirectPath: getDashboardPath(data.role) };
    } catch (err) {
      const error = err.response?.data?.message || "Login failed";
      return { success: false, error };
    }
  };

  const logout = async () => {
    await apiLogout();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, isAuthenticated: !!user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
