import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { getAvailableCourses, getEnrolledCourses, enrollInCourse } from "../../api";
import api from "../../api";

export default function Courses() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [courses, setCourses] = useState([]);
  const [enrolledIds, setEnrolledIds] = useState(new Set());
  const [thumbnails, setThumbnails] = useState({});
  const [loading, setLoading] = useState(true);
  const [enrollingId, setEnrollingId] = useState(null);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async () => {
    if (!user?.tenantId || !user?.id) return;
    setLoading(true);
    setError(null);
    try {
      const [allCourses, enrolled] = await Promise.all([
        getAvailableCourses(user.id),
        getEnrolledCourses(user.id),
      ]);
      setCourses(allCourses);
      setEnrolledIds(new Set(enrolled.map((c) => c.id)));
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load courses");
    } finally {
      setLoading(false);
    }
  }, [user?.tenantId, user?.id]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // Fetch thumbnails for courses that have them
  useEffect(() => {
    let isMounted = true;
    const urls = {};

    const fetchThumbnails = async () => {
      const toFetch = courses.filter((c) => c.thumbnailId && !thumbnails[c.id]);
      await Promise.all(
        toFetch.map(async (course) => {
          try {
            const response = await api.get(`/api/resources/${course.thumbnailId}`, {
              responseType: "blob",
            });
            if (isMounted) {
              urls[course.id] = URL.createObjectURL(response.data);
              setThumbnails((prev) => ({ ...prev, [course.id]: urls[course.id] }));
            }
          } catch {
            // ignore thumbnail load failures
          }
        })
      );
    };

    if (courses.length > 0) fetchThumbnails();

    return () => {
      isMounted = false;
      Object.values(urls).forEach(URL.revokeObjectURL);
    };
  }, [courses]);

  const handleEnroll = async (courseId) => {
    setEnrollingId(courseId);
    setError(null);
    try {
      await enrollInCourse(courseId, user.id);
      setEnrolledIds((prev) => new Set([...prev, courseId]));
    } catch (err) {
      setError(err.response?.data?.message || "Failed to enroll");
    } finally {
      setEnrollingId(null);
    }
  };

  if (loading) {
    return (
      <div>
        <div className="page-header">
          <div className="page-header-left">
            <div>
              <h1 className="page-header-title">Courses</h1>
              <p className="page-header-desc">Browse and enroll in available courses</p>
            </div>
          </div>
        </div>
        <div className="loading-enterprise">
          <div className="spinner-enterprise"></div>
          <span>Loading courses…</span>
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="page-header">
        <div className="page-header-left">
          <div>
            <h1 className="page-header-title">Courses</h1>
            <p className="page-header-desc">Browse and enroll in available courses</p>
          </div>
        </div>
      </div>

      {error && (
        <div className="alert-enterprise alert-enterprise-danger" style={{ marginBottom: 16 }}>
          {error}
        </div>
      )}

      {courses.length === 0 ? (
        <div className="card-enterprise">
          <div className="card-enterprise-body" style={{ textAlign: "center", padding: "48px 24px", color: "#95a5a6" }}>
            No courses available yet.
          </div>
        </div>
      ) : (
        <div className="learner-course-grid">
          {courses.map((course) => {
            const isEnrolled = enrolledIds.has(course.id);
            return (
              <div key={course.id} className="learner-course-card">
                <div
                  className="learner-course-thumb"
                  style={
                    thumbnails[course.id]
                      ? { backgroundImage: `url(${thumbnails[course.id]})` }
                      : {}
                  }
                >
                  {!thumbnails[course.id] && "No Image"}
                </div>
                <div className="learner-course-body">
                  <h3 className="learner-course-title">{course.title}</h3>
                  <p className="learner-course-desc">
                    {course.description?.length > 120
                      ? course.description.slice(0, 120) + "…"
                      : course.description || "No description"}
                  </p>
                  <div className="learner-course-footer">
                    {isEnrolled ? (
                      <>
                        <span className="learner-badge-enrolled">✓ Enrolled</span>
                        <button
                          className="btn-enterprise btn-enterprise-primary btn-enterprise-sm"
                          style={{ marginLeft: "auto" }}
                          onClick={() => navigate(`/learner/courses/${course.id}`)}
                        >
                          View Course
                        </button>
                      </>
                    ) : (
                      <button
                        className="btn-enterprise btn-enterprise-dark btn-enterprise-sm"
                        style={{ marginLeft: "auto" }}
                        onClick={() => handleEnroll(course.id)}
                        disabled={enrollingId === course.id}
                      >
                        {enrollingId === course.id ? "Enrolling…" : "Enroll Now"}
                      </button>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}