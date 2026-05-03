import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { getCourse, getEnrolledCourses, enrollInCourse } from "../../api";
import api from "../../api";

export default function CourseOverview() {
  const navigate = useNavigate();
  const { courseId } = useParams();
  const { user } = useAuth();

  const [course, setCourse] = useState(null);
  const [thumbnail, setThumbnail] = useState(null);
  const [enrolled, setEnrolled] = useState(false);
  const [loading, setLoading] = useState(true);
  const [enrolling, setEnrolling] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        const [courseData, enrolledCourses] = await Promise.all([
          getCourse(courseId),
          getEnrolledCourses(user.id),
        ]);
        if (!isMounted) return;
        setCourse(courseData);
        setEnrolled(enrolledCourses.some((c) => c.id === courseId));

        // fetch thumbnail
        if (courseData.thumbnailId) {
          try {
            const res = await api.get(`/api/resources/${courseData.thumbnailId}`, { responseType: "blob" });
            if (isMounted) setThumbnail(URL.createObjectURL(res.data));
          } catch {
            // ignore
          }
        }
      } catch (err) {
        if (isMounted) setError(err.response?.data?.message || "Failed to load course");
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    fetchData();
    return () => {
      isMounted = false;
    };
  }, [courseId, user.id]);

  // Clean up thumbnail URL
  useEffect(() => {
    return () => {
      if (thumbnail) URL.revokeObjectURL(thumbnail);
    };
  }, [thumbnail]);

  const handleEnroll = async () => {
    setEnrolling(true);
    setError(null);
    try {
      await enrollInCourse(courseId, user.id);
      setEnrolled(true);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to enroll");
    } finally {
      setEnrolling(false);
    }
  };

  if (loading) {
    return (
      <div>
        <div className="page-header">
          <div className="page-header-left">
            <div>
              <h1 className="page-header-title">Course Overview</h1>
            </div>
          </div>
        </div>
        <div className="loading-enterprise">
          <div className="spinner-enterprise"></div>
          <span>Loading course…</span>
        </div>
      </div>
    );
  }

  if (!course) {
    return (
      <div>
        <div className="page-header">
          <div className="page-header-left">
            <div>
              <h1 className="page-header-title">Course Not Found</h1>
            </div>
          </div>
        </div>
        <div className="alert-enterprise alert-enterprise-danger">Course not found.</div>
      </div>
    );
  }

  return (
    <div>
      <div className="page-header">
        <div className="page-header-left">
          <div>
            <button
              onClick={() => navigate("/learner/courses")}
              className="btn-enterprise btn-enterprise-ghost"
              style={{ marginBottom: 8, padding: "4px 0", fontSize: 13 }}
            >
              ← Back to Courses
            </button>
            <h1 className="page-header-title">{course.title}</h1>
          </div>
        </div>
      </div>

      {error && (
        <div className="alert-enterprise alert-enterprise-danger" style={{ marginBottom: 16 }}>
          {error}
        </div>
      )}

      <div className="card-enterprise" style={{ overflow: "hidden" }}>
        {/* Hero thumbnail */}
        <div
          style={{
            height: 220,
            background: thumbnail
              ? `url(${thumbnail}) center/cover no-repeat`
              : "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            color: "#fff",
            fontSize: 14,
          }}
        >
          {!thumbnail && "No Image"}
        </div>

        <div className="card-enterprise-body" style={{ padding: "24px 28px" }}>
          <h2 style={{ fontSize: 20, fontWeight: 600, margin: "0 0 8px 0", color: "#2c3e50" }}>{course.title}</h2>

          {enrolled && (
            <span className="learner-badge-enrolled" style={{ marginBottom: 12, display: "inline-flex" }}>✓ Enrolled</span>
          )}

          <p style={{ fontSize: 14, color: "#555", margin: "12px 0 20px 0", lineHeight: 1.7 }}>
            {course.description || "No description available."}
          </p>

          <div style={{ display: "flex", gap: 10 }}>
            {enrolled ? (
              <button
                className="btn-enterprise btn-enterprise-primary"
                onClick={() => navigate(`/learner/courses/${courseId}/content`)}
              >
                Go to Content
              </button>
            ) : (
              <button
                className="btn-enterprise btn-enterprise-dark"
                onClick={handleEnroll}
                disabled={enrolling}
              >
                {enrolling ? "Enrolling…" : "Enroll in Course"}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}