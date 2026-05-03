function Sidebar({ onManageTenants, onLogout }) {
    return (
        <aside
            className="w-64 h-full flex flex-col justify-between text-white"
            style={{ backgroundColor: "#434E78" }}
        >
            <div>
                <div className="flex items-center px-6 py-6 text-lg font-semibold">
                    <div className="w-9 h-9 rounded-full bg-[#f7f8f9] text-[rgb(12,12,12)] flex items-center justify-center mr-3">
                        👤
                    </div>
                    <span>Super Admin</span>
                </div>


                <button
                    onClick={onManageTenants}
                    className="w-full bg-white text-[#434E78] py-2 rounded-md font-semibold"
                >
                    Manage Tenants
                </button>
            </div>

            <div className="p-6">
                <button
                    onClick={onLogout}
                    className="w-full bg-white text-[#434E78] py-2 rounded-md font-semibold"
                >
                    Logout
                </button>
            </div>
        </aside>
    );
}

export default Sidebar;
