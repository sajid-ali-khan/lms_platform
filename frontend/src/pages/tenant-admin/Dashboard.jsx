import { useState, useEffect } from "react";
import { useAuth } from "../../context/AuthContext";
import { getDashboard } from "../../api";

const StatCard = ({ value, label, accent }) => (
  <div className="card-enterprise" style={{ minWidth: "160px", flex: "1" }}>
    <div className="card-enterprise-body text-center">
      <p
        className="mb-1"
        style={{ fontSize: "2rem", fontWeight: "700", color: accent || "var(--enterprise-primary)" }}
      >
        {value}
      </p>
      <p className="mb-0" style={{ fontSize: "0.8rem", color: "var(--enterprise-muted)", textTransform: "uppercase", letterSpacing: "0.05em" }}>
        {label}
      </p>
    </div>
  </div>
);

export default function Dashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!user?.tenantId) return;
    getDashboard(user.tenantId)
      .then(setStats)
      .catch(() => setError("Failed to load dashboard"))
      .finally(() => setLoading(false));
  }, [user?.tenantId]);

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: "300px" }}>
        <div className="loading-enterprise"></div>
        <span className="ms-3" style={{ color: "var(--enterprise-muted)" }}>Loading dashboard...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert-enterprise-danger">{error}</div>
    );
  }

  // orgUnitsCountMaps: Map<structureName, Map<typeName, count>>
  const orgEntries = stats?.orgUnitsCountMaps ? Object.entries(stats.orgUnitsCountMaps) : [];

  return (
    <div>
      <div className="page-header">
        <h1 className="page-header-title">Dashboard</h1>
        <p style={{ color: "var(--enterprise-muted)", marginBottom: 0 }}>
          Overview of {user?.tenantName || "your organization"}
        </p>
      </div>

      {/* Users section */}
      <div className="card-enterprise mb-4">
        <div className="card-enterprise-header">
          <h2 className="mb-0" style={{ fontSize: "0.85rem", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.08em", color: "var(--enterprise-muted)" }}>Users</h2>
        </div>
        <div className="card-enterprise-body">
          <div className="d-flex gap-3 flex-wrap">
            <StatCard value={stats?.userCount ?? 0} label="Total Users" accent="var(--enterprise-primary)" />
            <StatCard value={stats?.learnerCount ?? 0} label="Learners" accent="#6c757d" />
            <StatCard value={stats?.instructorCount ?? 0} label="Instructors" accent="#495057" />
          </div>
        </div>
      </div>

      {/* Courses section */}
      <div className="card-enterprise mb-4">
        <div className="card-enterprise-header">
          <h2 className="mb-0" style={{ fontSize: "0.85rem", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.08em", color: "var(--enterprise-muted)" }}>Courses</h2>
        </div>
        <div className="card-enterprise-body">
          <div className="d-flex gap-3 flex-wrap">
            <StatCard value={stats?.courseCount ?? 0} label="Total Courses" accent="var(--enterprise-primary)" />
            <StatCard value={stats?.activeCourseCount ?? 0} label="Active Courses" accent="#28a745" />
          </div>
        </div>
      </div>

      {/* Organization Units section */}
      {orgEntries.length > 0 && orgEntries.map(([structureName, typeCounts]) => (
        <div key={structureName} className="card-enterprise mb-4">
          <div className="card-enterprise-header">
            <h2 className="mb-0" style={{ fontSize: "0.85rem", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.08em", color: "var(--enterprise-muted)" }}>
              {structureName} — Organization Units
            </h2>
          </div>
          <div className="card-enterprise-body">
            <div className="d-flex gap-3 flex-wrap">
              {Object.entries(typeCounts).map(([typeName, count]) => (
                <StatCard key={typeName} value={count} label={typeName} accent="#495057" />
              ))}
            </div>
          </div>
        </div>
      ))}

      {orgEntries.length === 0 && (
        <div className="card-enterprise mb-4">
          <div className="card-enterprise-header">
            <h2 className="mb-0" style={{ fontSize: "0.85rem", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.08em", color: "var(--enterprise-muted)" }}>Organization Units</h2>
          </div>
          <div className="card-enterprise-body">
            <div className="empty-state-enterprise">
              <p>No organization structures configured yet.</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
