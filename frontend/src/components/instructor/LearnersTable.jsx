// src/components/instructor/LearnersTable.jsx
export default function LearnersTable({ learners }) {
    return (
        <div className="card-enterprise">
            <div className="card-enterprise-header">
                <h3>Enrolled Learners</h3>
            </div>
            <div style={{ overflowX: "auto" }}>
                <table style={{ width: "100%", borderCollapse: "collapse" }}>
                    <thead>
                        <tr style={{ borderBottom: "2px solid #eee", background: "#fafafa" }}>
                            <th style={{ padding: "12px 16px", textAlign: "left", fontSize: "12px", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.5px", color: "#555" }}>Name</th>
                            <th style={{ padding: "12px 16px", textAlign: "left", fontSize: "12px", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.5px", color: "#555" }}>Email</th>
                            <th style={{ padding: "12px 16px", textAlign: "left", fontSize: "12px", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.5px", color: "#555" }}>Belongs to</th>
                        </tr>
                    </thead>
                    <tbody>
                        {learners.map((l) => (
                            <tr key={l.email} style={{ borderBottom: "1px solid #eee" }}>
                                <td style={{ padding: "12px 16px", fontSize: "14px", color: "#333" }}>{l.name}</td>
                                <td style={{ padding: "12px 16px", fontSize: "14px", color: "#333" }}>{l.email}</td>
                                <td style={{ padding: "12px 16px", fontSize: "14px", color: "#7f8c8d" }}>{l.belongsTo}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
