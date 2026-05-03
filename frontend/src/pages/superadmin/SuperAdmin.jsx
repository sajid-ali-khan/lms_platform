import { useNavigate, useLocation } from "react-router-dom";
import TenantTable from "../../components/superadmin/TenantTable";
import CreateTenantForm from "../../components/superadmin/CreateTenantForm";

function SuperAdmin() {
    const navigate = useNavigate();
    const location = useLocation();

    const isCreate = location.pathname === "/superadmin/tenants/new";

    return (
        <div>
            {isCreate ? (
                <CreateTenantForm
                    onBack={() => navigate("/superadmin/tenants")}
                />
            ) : (
                <TenantTable onNewTenant={() => navigate("/superadmin/tenants/new")} />
            )}
        </div>
    );
}

export default SuperAdmin;
