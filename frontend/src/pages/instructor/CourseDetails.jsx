import { useState, useEffect, useCallback, useRef } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Header from "../../components/instructor/Header.jsx";
import Tabs from "../../components/instructor/Tabs.jsx";
import Overview from "./tabs/Overview.jsx";
import ContentTab from "./tabs/ContentTab.jsx";
import Learners from "./tabs/Learners.jsx";
import Allocations from "./tabs/Allocations.jsx";
import { getCourse, updateCourse } from "../../api.js";
import { useAuth } from "../../context/AuthContext.jsx";

export default function CourseDetails() {
    const navigate = useNavigate();
    const { courseId } = useParams();
    const { user } = useAuth();
    const [tab, setTab] = useState("overview");
    const [course, setCourse] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [notification, setNotification] = useState(null);
    const notificationTimer = useRef(null);

    const tabs = [
        { value: "overview", label: "Overview" },
        { value: "content", label: "Content" },
        { value: "learners", label: "Learners" },
        { value: "allocations", label: "Allocations" },
    ];

    const fetchCourse = useCallback(async () => {
        if (!courseId) return;
        setLoading(true);
        setError(null);
        try {
            const data = await getCourse(courseId);
            setCourse(data);
        } catch (err) {
            setError(err.response?.data?.message || "Failed to load course");
        } finally {
            setLoading(false);
        }
    }, [courseId]);

    useEffect(() => {
        fetchCourse();
    }, [fetchCourse]);

    const showNotification = useCallback((message) => {
        if (notificationTimer.current) {
            clearTimeout(notificationTimer.current);
        }
        setNotification(message);
        notificationTimer.current = setTimeout(() => setNotification(null), 4000);
    }, []);

    useEffect(() => {
        return () => {
            if (notificationTimer.current) {
                clearTimeout(notificationTimer.current);
            }
        };
    }, []);

    const handleMetadataSave = async ({ title, description, status, thumbnailFile }) => {
        const payload = { title: title?.trim(), description: description?.trim(), status };
        try {
            await updateCourse(courseId, payload, thumbnailFile || null);
            await fetchCourse();
            showNotification("Course updated successfully.");
            return { success: true };
        } catch (err) {
            return { success: false, error: err.response?.data?.message || "Failed to update" };
        }
    };

    if (loading) {
        return (
            <div>
                <Header
                    title="Course Details"
                    description="Loading the latest metadata"
                    showBack={true}
                    onBack={() => navigate("/instructor/courses")}
                    actions={null}
                />
                <div className="card-enterprise">
                    <div className="card-enterprise-body">
                        <div className="loading-enterprise">
                            <div className="spinner-enterprise"></div>
                            <span>Loading course…</span>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div>
                <Header
                    title="Course Details"
                    description="Something went wrong"
                    showBack={true}
                    onBack={() => navigate("/instructor/courses")}
                    actions={null}
                />
                <div className="card-enterprise">
                    <div className="card-enterprise-body">
                        <div className="alert-enterprise alert-enterprise-danger">{error}</div>
                    </div>
                </div>
            </div>
        );
    }

    if (!course) {
        return null;
    }

    return (
        <div>
            <Header
                title={course.title || "Course Details"}
                description="Edit the essentials or dive into content management"
                showBack={true}
                onBack={() => navigate("/instructor/courses")}
                actions={null}
            />

            {notification && (
                <div className="notification-enterprise" role="status">
                    {notification}
                </div>
            )}

            <div className="card-enterprise">
                <Tabs tabs={tabs} active={tab} onChange={setTab} />
                <div className="card-enterprise-body">
                    {tab === "overview" && (
                        <Overview
                            course={course}
                            onSave={handleMetadataSave}
                        />
                    )}
                    {tab === "content" && <ContentTab course={course} />}
                    {tab === "learners" && <Learners courseId={courseId} />}
                    {tab === "allocations" && <Allocations courseId={courseId} tenantId={user?.tenantId} />}
                </div>
            </div>
        </div>
    );
}
