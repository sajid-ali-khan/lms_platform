import { useMemo, useEffect, useState } from "react";
import api from "../../api";

const STATUS_STYLES = {
    ACTIVE: "badge-active",
    INACTIVE: "badge-inactive",
    HIDDEN: "badge-hidden",
    DRAFT: "badge-draft",
};

const STATUS_LABELS = {
    ACTIVE: "Active",
    INACTIVE: "Inactive",
    HIDDEN: "Hidden",
    DRAFT: "Draft",
};

// Helper function to convert string to title case
const toTitleCase = (str) => {
    if (!str) return str;
    return str
        .toLowerCase()
        .split(' ')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
};

export default function CourseCard({ course, onEdit, onRemove }) {
    const [thumbnailSrc, setThumbnailSrc] = useState(null);

    // Build preview URL for local files or absolute URLs already available on the course
    const localCoverSrc = useMemo(() => {
        if (course.cover instanceof File) {
            return URL.createObjectURL(course.cover);
        }
        if (typeof course.cover === "string") {
            return course.cover;
        }
        return null;
    }, [course.cover]);

    // Fetch secure thumbnails via the resource controller so the auth header is included
    useEffect(() => {
        let isMounted = true;
        let objectUrl;

        const shouldFetchRemoteThumbnail =
            !!course.thumbnailId && !localCoverSrc;

        if (!shouldFetchRemoteThumbnail) {
            setThumbnailSrc(null);
            return undefined;
        }

        const fetchThumbnail = async () => {
            try {
                const response = await api.get(
                    `/api/resources/${course.thumbnailId}`,
                    { responseType: "blob" }
                );
                objectUrl = URL.createObjectURL(response.data);
                if (isMounted) {
                    setThumbnailSrc(objectUrl);
                }
            } catch (err) {
                console.error("Failed to load course thumbnail", err);
                if (isMounted) {
                    setThumbnailSrc(null);
                }
            }
        };

        fetchThumbnail();

        return () => {
            isMounted = false;
            if (objectUrl) {
                URL.revokeObjectURL(objectUrl);
            }
        };
    }, [course.thumbnailId, localCoverSrc]);

    useEffect(() => {
        if (course.cover instanceof File && localCoverSrc) {
            return () => URL.revokeObjectURL(localCoverSrc);
        }
        return undefined;
    }, [course.cover, localCoverSrc]);

    const coverSrc = localCoverSrc || thumbnailSrc;
    const rawStatus = course.currentStatus || course.status || course.visibility;
    const normalizedStatus = typeof rawStatus === "string" ? rawStatus.toUpperCase() : rawStatus;
    const statusLabel = STATUS_LABELS[normalizedStatus] || normalizedStatus || "--";
    const statusClass = STATUS_STYLES[normalizedStatus] || "badge-hidden";

    return (
        <div className="course-card">
            {/* Thumbnail image */}
            <div className="course-card-thumbnail">
                {coverSrc ? (
                    <img src={coverSrc} alt={course.title} />
                ) : (
                    <span>No Image</span>
                )}
            </div>

            {/* Course info */}
            <div className="course-card-body">
                <div className="course-card-header">
                    <h6 className="course-card-title">{toTitleCase(course.title)}</h6>
                    <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                        <span className={`badge-enterprise ${statusClass}`}>
                            {statusLabel}
                        </span>
                        {/* Action icons */}
                        <button
                            onClick={() => onEdit(course)}
                            className="icon-btn"
                            title="Edit course"
                            aria-label="Edit course"
                        >
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                <path d="M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168l10-10zM11.207 2.5 13.5 4.793 14.793 3.5 12.5 1.207 11.207 2.5zm1.586 3L10.5 3.207 4 9.707V10h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.293l6.5-6.5zm-9.761 5.175-.106.106-1.528 3.821 3.821-1.528.106-.106A.5.5 0 0 1 5 12.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.468-.325z"/>
                            </svg>
                        </button>
                        <button
                            onClick={() => onRemove(course.id)}
                            className="icon-btn icon-btn-danger"
                            title="Remove course"
                            aria-label="Remove course"
                        >
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"/>
                                <path fillRule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"/>
                            </svg>
                        </button>
                    </div>
                </div>
                <p className="course-card-desc">
                    {course.description || "No description"}
                </p>
            </div>
        </div>
    );
}
