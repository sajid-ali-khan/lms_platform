import { Outlet, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function LearnerLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();

  const toTitleCase = (str) => {
    if (!str) return str;
    return str.toLowerCase().split(" ").map((w) => w.charAt(0).toUpperCase() + w.slice(1)).join(" ");
  };

  const isActive = (path) =>
    location.pathname === path || location.pathname.startsWith(path + "/");

  return (
    <div className="learner-shell">
      {/* ── Top Navbar ── */}
      <header className="learner-navbar">
        <div className="learner-navbar-inner">
          <div className="learner-navbar-brand" onClick={() => navigate("/learner/courses")}>
            <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 16 16">
              <path d="M1 2.828c.885-.37 2.154-.769 3.388-.893 1.33-.134 2.458.063 3.112.752v9.746c-.935-.53-2.12-.603-3.213-.493-1.18.12-2.37.461-3.287.811V2.828zm7.5-.141c.654-.689 1.782-.886 3.112-.752 1.234.124 2.503.523 3.388.893v9.923c-.918-.35-2.107-.692-3.287-.81-1.094-.111-2.278-.039-3.213.492V2.687zM8 1.783C7.015.936 5.587.81 4.287.94c-1.514.153-3.042.672-3.994 1.105A.5.5 0 0 0 0 2.5v11a.5.5 0 0 0 .707.455c.882-.4 2.303-.881 3.68-1.02 1.409-.142 2.59.087 3.223.877a.5.5 0 0 0 .78 0c.633-.79 1.814-1.019 3.222-.877 1.378.139 2.8.62 3.681 1.02A.5.5 0 0 0 16 13.5v-11a.5.5 0 0 0-.293-.455c-.952-.433-2.48-.952-3.994-1.105C10.413.809 8.985.936 8 1.783z"/>
            </svg>
            <span>{user?.tenantName || "Learning Platform"}</span>
          </div>

          <nav className="learner-navbar-links">
            <button
              className={`learner-nav-link ${isActive("/learner/courses") ? "active" : ""}`}
              onClick={() => navigate("/learner/courses")}
            >
              My Courses
            </button>
            <button
              className={`learner-nav-link ${isActive("/learner/settings") ? "active" : ""}`}
              onClick={() => navigate("/learner/settings")}
            >
              Settings
            </button>
          </nav>

          <div className="learner-navbar-right">
            <span className="learner-navbar-user">
              {toTitleCase(user?.fullName)}
            </span>
            <button
              className="learner-navbar-logout"
              onClick={() => { logout(); navigate("/login"); }}
            >
              Sign Out
            </button>
          </div>
        </div>
      </header>

      {/* ── Page Content ── */}
      <main className="learner-main">
        <div className="learner-content">
          <Outlet />
        </div>
      </main>

      {/* ── Footer ── */}
      <footer className="learner-footer">
        <div className="learner-footer-inner">
          <span>&copy; {new Date().getFullYear()} {user?.tenantName || "LMS Platform"}. All rights reserved.</span>
          <div className="learner-footer-links">
            <span>Terms of Service</span>
            <span>Privacy Policy</span>
            <span>Help Center</span>
          </div>
        </div>
      </footer>
    </div>
  );
}
