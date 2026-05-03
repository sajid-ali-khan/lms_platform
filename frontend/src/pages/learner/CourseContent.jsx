import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getModules } from "../../api";

/** Convert any YouTube URL to an embeddable URL */
function toYouTubeEmbed(url) {
  if (!url) return url;
  // Already an embed link
  if (url.includes("youtube.com/embed/")) return url;
  // https://youtu.be/VIDEO_ID or https://www.youtube.com/watch?v=VIDEO_ID
  let videoId = null;
  try {
    const u = new URL(url);
    if (u.hostname === "youtu.be") {
      videoId = u.pathname.slice(1);
    } else if (u.searchParams.has("v")) {
      videoId = u.searchParams.get("v");
    }
  } catch {
    return url;
  }
  return videoId ? `https://www.youtube.com/embed/${videoId}` : url;
}

export default function CourseContent() {
  const navigate = useNavigate();
  const { courseId } = useParams();

  const [modules, setModules] = useState([]);
  const [activeLesson, setActiveLesson] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;
    const fetchModules = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await getModules(courseId);
        if (!isMounted) return;
        // Sort modules by sequenceOrder, lessons already sorted by backend
        const sorted = [...data].sort((a, b) => a.sequenceOrder - b.sequenceOrder);
        setModules(sorted);

        // Auto-select first lesson
        for (const mod of sorted) {
          const published = (mod.lessons || []).filter((l) => l.isPublished !== false);
          if (published.length > 0) {
            setActiveLesson(published[0]);
            break;
          }
        }
      } catch (err) {
        if (isMounted) setError(err.response?.data?.message || "Failed to load content");
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    fetchModules();
    return () => { isMounted = false; };
  }, [courseId]);

  if (loading) {
    return (
      <div>
        <div className="page-header">
          <div className="page-header-left">
            <div>
              <h1 className="page-header-title">Course Content</h1>
            </div>
          </div>
        </div>
        <div className="loading-enterprise">
          <div className="spinner-enterprise"></div>
          <span>Loading content…</span>
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="page-header">
        <div className="page-header-left">
          <div>
            <button
              onClick={() => navigate(`/learner/courses/${courseId}`)}
              className="btn-enterprise btn-enterprise-ghost"
              style={{ marginBottom: 8, padding: "4px 0", fontSize: 13 }}
            >
              ← Back to Overview
            </button>
            <h1 className="page-header-title">Course Content</h1>
          </div>
        </div>
      </div>

      {error && (
        <div className="alert-enterprise alert-enterprise-danger" style={{ marginBottom: 16 }}>
          {error}
        </div>
      )}

      <div style={{ display: "flex", gap: 16, alignItems: "flex-start" }}>
        {/* Sidebar: module/lesson tree */}
        <div
          className="card-enterprise"
          style={{ width: 280, flexShrink: 0, maxHeight: "calc(100vh - 200px)", overflowY: "auto" }}
        >
          <div className="card-enterprise-body" style={{ padding: 12 }}>
            <p style={{ fontSize: 13, fontWeight: 600, margin: "0 0 12px 0", color: "#2c3e50" }}>
              Modules
            </p>

            {modules.length === 0 ? (
              <p style={{ fontSize: 13, color: "#95a5a6" }}>No modules yet.</p>
            ) : (
              modules.map((mod, mi) => {
                const lessons = (mod.lessons || []).filter((l) => l.isPublished !== false);
                return (
                  <div key={mod.id} style={{ marginBottom: 14 }}>
                    <p style={{ fontSize: 12, fontWeight: 600, color: "#7f8c8d", margin: "0 0 6px 0", textTransform: "uppercase", letterSpacing: 0.5 }}>
                      Module {mi + 1}: {mod.title || "Untitled"}
                    </p>
                    {lessons.map((lesson) => (
                      <div
                        key={lesson.id}
                        onClick={() => setActiveLesson(lesson)}
                        style={{
                          padding: "6px 10px",
                          marginBottom: 2,
                          borderRadius: 4,
                          cursor: "pointer",
                          fontSize: 13,
                          background: activeLesson?.id === lesson.id ? "#eef2ff" : "transparent",
                          color: activeLesson?.id === lesson.id ? "#4f46e5" : "#2c3e50",
                          fontWeight: activeLesson?.id === lesson.id ? 600 : 400,
                          transition: "background 0.15s",
                        }}
                      >
                        {lesson.title || "Untitled Lesson"}
                        {lesson.type && (
                          <span style={{ fontSize: 11, color: "#95a5a6", marginLeft: 6 }}>
                            ({lesson.type.toLowerCase()})
                          </span>
                        )}
                      </div>
                    ))}
                    {lessons.length === 0 && (
                      <p style={{ fontSize: 12, color: "#bdc3c7", paddingLeft: 10 }}>No lessons</p>
                    )}
                  </div>
                );
              })
            )}
          </div>
        </div>

        {/* Main content area */}
        <div className="card-enterprise" style={{ flex: 1 }}>
          <div className="card-enterprise-body">
            {activeLesson ? (
              <>
                <h3 style={{ fontSize: 16, fontWeight: 600, margin: "0 0 16px 0" }}>
                  {activeLesson.title || "Untitled Lesson"}
                </h3>

                {/* Resource preview */}
                {activeLesson.resourceUrl && (
                  <div style={{ marginBottom: 16 }}>
                    {activeLesson.type === "VIDEO" ? (
                      <div style={{ position: "relative", paddingBottom: "56.25%", height: 0, overflow: "hidden", borderRadius: 4 }}>
                        <iframe
                          src={toYouTubeEmbed(activeLesson.resourceUrl)}
                          title={activeLesson.title || "Video"}
                          style={{ position: "absolute", top: 0, left: 0, width: "100%", height: "100%", border: 0 }}
                          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                          allowFullScreen
                        />
                      </div>
                    ) : (
                      <div style={{ marginBottom: 8 }}>
                        <a
                          href={activeLesson.resourceUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="btn-enterprise btn-enterprise-primary"
                          style={{ fontSize: 13 }}
                        >
                          Open Resource
                        </a>
                      </div>
                    )}
                  </div>
                )}

                {/* Lesson text content */}
                {activeLesson.content && (
                  <div style={{ fontSize: 14, lineHeight: 1.7, color: "#2c3e50" }}>
                    {activeLesson.content}
                  </div>
                )}

                {!activeLesson.content && !activeLesson.resourceUrl && (
                  <p style={{ color: "#95a5a6", fontSize: 13 }}>This lesson has no content yet.</p>
                )}
              </>
            ) : (
              <div style={{ textAlign: "center", padding: "48px 24px", color: "#95a5a6" }}>
                Select a lesson from the sidebar to view its content.
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}