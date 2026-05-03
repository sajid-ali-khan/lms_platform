import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { getCourses, deleteCourse } from "../../api";
import Header from "../../components/instructor/Header.jsx";
import CoursesGrid from "../../components/instructor/CoursesGrid.jsx";
import ConfirmDialog from "../../components/instructor/ConfirmDialog.jsx";

export default function Inactive() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deleteTarget, setDeleteTarget] = useState(null);

  useEffect(() => {
    const fetchCourses = async () => {
      if (!user?.tenantId) return;
      try {
        const data = await getCourses(user.tenantId);
        const inactive = data.filter(c => c.currentStatus === "INACTIVE" || !c.currentStatus);
        setCourses(inactive);
      } catch (err) {
        console.error("Error:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchCourses();
  }, [user]);

  const handleRemoveCourse = (courseId) => {
    setDeleteTarget(courseId);
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteCourse(deleteTarget);
      setCourses((prev) => prev.filter((c) => c.id !== deleteTarget));
    } catch (err) {
      console.error("Failed to delete course:", err);
    } finally {
      setDeleteTarget(null);
    }
  };

  if (loading) {
    return (
      <div>
        <Header title="Inactive" description="Courses that are currently inactive" />
        <div className="card-enterprise">
          <div className="card-enterprise-body">
            <div className="loading-enterprise">
              <div className="spinner-enterprise"></div>
              <span>Loading inactive courses…</span>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div>
      <Header
        title="Inactive"
        description="Courses that are currently inactive"
        actions={
          <button
            onClick={() => navigate("/instructor/courses/new")}
            className="btn-enterprise btn-enterprise-dark"
          >
            + New Course
          </button>
        }
      />
      {courses.length === 0 ? (
        <div className="card-enterprise">
          <div className="card-enterprise-body empty-state-enterprise">
            <h3>No inactive courses</h3>
            <p>All your courses are active</p>
          </div>
        </div>
      ) : (
        <CoursesGrid
          courses={courses}
          onEdit={(course) => navigate(`/instructor/courses/${course.id}`)}
          onRemove={handleRemoveCourse}
        />
      )}

      <ConfirmDialog
        isOpen={!!deleteTarget}
        title="Delete Course"
        message="Are you sure you want to delete this course? This action cannot be undone."
        confirmText="Delete"
        cancelText="Cancel"
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
        variant="danger"
      />
    </div>
  );
}
