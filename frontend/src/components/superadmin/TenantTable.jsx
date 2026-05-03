import { useState, useEffect } from "react";
import { getTenants } from "../../api";

const CATEGORY_BADGE = {
    CORPORATE: { label: "Corporate", color: "#2980b9" },
    EDUCATION:  { label: "Education",  color: "#27ae60" },
    TRAINING:   { label: "Training",   color: "#8e44ad" },
};

function TenantTable({ onNewTenant }) {
    const [tenants, setTenants] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchTenants = async () => {
            try {
                const data = await getTenants();
                setTenants(data);
            } catch (err) {
                setError(err.response?.data?.message || "Failed to load tenants.");
            } finally {
                setLoading(false);
            }
        };
        fetchTenants();
    }, []);

    return (
        <div>
            <div className="page-header">
                <div>
                    <h1 className="page-header-title">Tenants</h1>
                    <p className="page-header-desc">All organisations registered on the platform</p>
                </div>
                <div className="page-header-actions">
                    <button onClick={onNewTenant} className="btn-enterprise btn-enterprise-primary btn-enterprise-sm">
                        + New Tenant
                    </button>
                </div>
            </div>

            <div className="card-enterprise">
                {loading && (
                    <div className="card-enterprise-body">
                        <div className="loading-enterprise">
                            <div className="spinner-enterprise" />
                            <span>Loading tenants…</span>
                        </div>
                    </div>
                )}

                {error && (
                    <div className="card-enterprise-body">
                        <div className="alert-enterprise alert-enterprise-danger">{error}</div>
                    </div>
                )}

                {!loading && !error && tenants.length === 0 && (
                    <div className="card-enterprise-body empty-state-enterprise">
                        <h3>No tenants yet</h3>
                        <p>Create your first tenant to get started.</p>
                        <button onClick={onNewTenant} className="btn-enterprise btn-enterprise-primary">
                            + New Tenant
                        </button>
                    </div>
                )}

                {!loading && !error && tenants.length > 0 && (
                    <div className="table-responsive">
                        <table className="table-enterprise">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Admin</th>
                                    <th>Category</th>
                                </tr>
                            </thead>
                            <tbody>
                                {tenants.map((tenant) => {
                                    const cat = CATEGORY_BADGE[tenant.category];
                                    return (
                                        <tr key={tenant.id}>
                                            <td>
                                                <span className="table-enterprise-id">
                                                    {tenant.id.slice(0, 8)}…
                                                </span>
                                            </td>
                                            <td className="table-enterprise-name">{tenant.name}</td>
                                            <td>
                                                {tenant.admin
                                                    ? tenant.admin
                                                    : <span className="table-enterprise-empty">No admin</span>}
                                            </td>
                                            <td>
                                                <span
                                                    className="badge-enterprise"
                                                    style={{ background: cat?.color ?? "#7f8c8d", color: "#fff" }}
                                                >
                                                    {cat?.label ?? tenant.category}
                                                </span>
                                            </td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}

export default TenantTable;
