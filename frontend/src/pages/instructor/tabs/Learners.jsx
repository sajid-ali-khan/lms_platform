import { useState, useEffect, useCallback } from "react";
import { getEnrollmentsByCourse } from "../../../api.js";

export default function Learners({ courseId }) {
    const [enrollments, setEnrollments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchEnrollments = useCallback(async () => {
        if (!courseId) return;
        setLoading(true);
        setError(null);
        try {
            const data = await getEnrollmentsByCourse(courseId);
            setEnrollments(data);
        } catch {
            setError("Failed to load enrollments");
        } finally {
            setLoading(false);
        }
    }, [courseId]);

    useEffect(() => {
        fetchEnrollments();
    }, [fetchEnrollments]);

    if (loading) {
        return (
            <div style={{ textAlign: "center", padding: "32px", color: "var(--enterprise-muted)" }}>
                Loading enrollments…
            </div>
        );
    }

    if (error) {
        return (
            <div className="alert-enterprise alert-enterprise-danger">{error}</div>
        );
    }

    return (
        <div className="card-enterprise">
            <div className="card-enterprise-header">
                <h3>Enrolled Learners ({enrollments.length})</h3>
            </div>
            <div style={{ overflowX: "auto" }}>
                <table style={{ width: "100%", borderCollapse: "collapse" }}>
                    <thead>
                        <tr style={{ borderBottom: "2px solid #eee", background: "#fafafa" }}>
                            <th style={{ padding: "12px 16px", textAlign: "left", fontSize: "12px", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.5px", color: "#555" }}>Name</th>
                            <th style={{ padding: "12px 16px", textAlign: "left", fontSize: "12px", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.5px", color: "#555" }}>Email</th>
                            <th style={{ padding: "12px 16px", textAlign: "left", fontSize: "12px", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.5px", color: "#555" }}>Org Unit</th>
                            <th style={{ padding: "12px 16px", textAlign: "left", fontSize: "12px", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.5px", color: "#555" }}>Status</th>
                            <th style={{ padding: "12px 16px", textAlign: "left", fontSize: "12px", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.5px", color: "#555" }}>Enrolled At</th>
                        </tr>
                    </thead>
                    <tbody>
                        {enrollments.length === 0 ? (
                            <tr>
                                <td colSpan="5" style={{ padding: "32px 16px", textAlign: "center", color: "#95a5a6" }}>
                                    No learners enrolled yet.
                                </td>
                            </tr>
                        ) : (
                            enrollments.map((e) => (
                                <tr key={e.id} style={{ borderBottom: "1px solid #eee" }}>
                                    <td style={{ padding: "12px 16px", fontSize: "14px", color: "#333" }}>{e.learnerName}</td>
                                    <td style={{ padding: "12px 16px", fontSize: "14px", color: "#333" }}>{e.learnerEmail}</td>
                                    <td style={{ padding: "12px 16px", fontSize: "14px", color: "#7f8c8d" }}>{e.orgUnitPath}</td>
                                    <td style={{ padding: "12px 16px", fontSize: "14px" }}>
                                        <span style={{
                                            display: "inline-block",
                                            padding: "2px 10px",
                                            borderRadius: "12px",
                                            fontSize: "12px",
                                            fontWeight: 600,
                                            background: e.status === "complete" ? "#d4edda" : "#fff3cd",
                                            color: e.status === "complete" ? "#155724" : "#856404",
                                        }}>
                                            {e.status}
                                        </span>
                                    </td>
                                    <td style={{ padding: "12px 16px", fontSize: "14px", color: "#7f8c8d" }}>
                                        {new Date(e.enrolledAt).toLocaleDateString()}
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
