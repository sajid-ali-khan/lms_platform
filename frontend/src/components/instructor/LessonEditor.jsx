import FormInput from "./FormInput.jsx";

export default function LessonEditor({ lesson, onChange }) {
    if (!lesson) {
        return (
            <div className="card-enterprise">
                <div className="card-enterprise-body" style={{ minHeight: "200px", display: "flex", alignItems: "center", justifyContent: "center", color: "#95a5a6" }}>
                    Select a lesson to edit.
                </div>
            </div>
        );
    }
    return (
        <div className="card-enterprise">
            <div className="card-enterprise-header">
                <h3>Lesson {lesson.sequenceOrder}: {lesson.title}</h3>
            </div>
            <div className="card-enterprise-body">
                <FormInput
                    label="Title"
                    value={lesson.title}
                    onChange={(v) => onChange({ ...lesson, title: v })}
                />
                <FormInput
                    label="Content"
                    multiline
                    value={lesson.content || ""}
                    onChange={(v) => onChange({ ...lesson, content: v })}
                />
                <div className="form-group-enterprise">
                    <label className="form-label-enterprise">Content Type</label>
                    <select
                        className="form-control-enterprise"
                        value={lesson.type}
                        onChange={(e) => onChange({ ...lesson, type: e.target.value })}
                    >
                        <option value="VIDEO">Video</option>
                        <option value="TEXT">Text</option>
                        <option value="DOCUMENT">Document</option>
                        <option value="QUIZ">Quiz</option>
                        <option value="ASSIGNMENT">Assignment</option>
                    </select>
                </div>
                <FormInput
                    label="Resource Link"
                    placeholder="Paste the resource link here"
                    value={lesson.resourceUrl || ""}
                    onChange={(v) => onChange({ ...lesson, resourceUrl: v })}
                />
            </div>
        </div>
    );
}
