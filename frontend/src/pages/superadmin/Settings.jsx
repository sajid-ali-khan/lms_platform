import { useAuth } from "../../context/AuthContext";
import ChangePassword from "../../components/ChangePassword";

export default function SuperAdminSettings() {
  const { user } = useAuth();

  return (
    <div>
      <div className="page-header">
        <div className="page-header-left">
          <h1 className="page-header-title">Settings</h1>
        </div>
      </div>
      <div className="row">
        <div className="col-lg-6 col-xl-5">
          <div className="card-enterprise">
            <div className="card-enterprise-header">
              <h3>Profile Information</h3>
            </div>
            <div className="card-enterprise-body">
              <div className="form-group-enterprise">
                <label className="form-label-enterprise">Full Name</label>
                <input
                  type="text"
                  value={user?.fullName || ""}
                  disabled
                  className="form-control-enterprise"
                />
              </div>
              <div className="form-group-enterprise">
                <label className="form-label-enterprise">Email</label>
                <input
                  type="email"
                  value={user?.email || ""}
                  disabled
                  className="form-control-enterprise"
                />
              </div>
            </div>
          </div>

          <div style={{ marginTop: "24px" }}>
            <ChangePassword />
          </div>
        </div>
      </div>
    </div>
  );
}
