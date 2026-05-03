import { useAuth } from "../../context/AuthContext";
import ChangePassword from "../../components/ChangePassword";

export default function LearnerSettings() {
  const { user } = useAuth();

  return (
    <div>
      <h2 style={{ fontSize: "20px", fontWeight: 600, marginBottom: "24px" }}>Settings</h2>
      <div style={{ maxWidth: "480px" }}>
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
  );
}
