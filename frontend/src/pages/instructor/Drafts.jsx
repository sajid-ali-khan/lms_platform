import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { getCourses, deleteCourse } from "../../api";
import Header from "../../components/instructor/Header.jsx";
import CoursesGrid from "../../components/instructor/CoursesGrid.jsx";
import ConfirmDialog from "../../components/instructor/ConfirmDialog.jsx";

export default function Drafts() {
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
        const drafts = data.filter(c => c.currentStatus === "DRAFT");
        setCourses(drafts);
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
        <Header title="Drafts" description="Courses that are still in draft" />
        <div className="card-enterprise">
          <div className="card-enterprise-body">
            <div className="loading-enterprise">
              <div className="spinner-enterprise"></div>
              <span>Loading draft courses…</span>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div>
      <Header
        title="Drafts"
        description="Courses that are still in draft"
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
            <h3>No draft courses</h3>
            <p>All your courses have been published or are in another status</p>
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
