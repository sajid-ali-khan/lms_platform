export default function Sidebar({ setPage, page }) {
  const btn = (id, label) => (
    <button
      onClick={() => setPage(id)}
      className={`w-full py-3 rounded-md text-sm font-medium
        ${page === id ? "border border-white" : ""}
      `}
      style={{
        backgroundColor: "rgba(0,0,0,0.7)",
        color: "white",
      }}
    >
      {label}
    </button>
  );

  return (
    <div
      className="w-64 h-screen p-6 space-y-6"
      style={{ backgroundColor: "#434E78" }}   // ✅ sidebar color
    >
      <div className="flex items-center gap-3">
        <div className="w-10 h-10 rounded-full bg-white/20 flex items-center justify-center" >👤</div>
        <h1 className="text-2xl font-semibold text-white">Tenant Name</h1>
      </div>

      <div className="space-y-4">
        {btn("dashboard", "Dashboard")}
        {btn("org", "Organization Structure")}
        {btn("users", "User Management")}
      </div>
    </div>
  );
}
