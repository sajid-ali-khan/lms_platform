// src/components/instructor/CoursesGrid.jsx
import CourseCard from "./CourseCard.jsx";

export default function CoursesGrid({ courses, onEdit, onRemove }) {
    return (
        <div className="row g-4">
            {courses.map((c) => (
                <div key={c.id} className="col-sm-6 col-lg-6 col-xl-4">
                    <CourseCard
                        course={c}
                        onEdit={onEdit}
                        onRemove={onRemove}
                    />
                </div>
            ))}
        </div>
    );
}
