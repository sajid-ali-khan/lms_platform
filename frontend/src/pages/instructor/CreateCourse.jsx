import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import FormInput from "../../components/instructor/FormInput.jsx";
import RadioGroup from "../../components/instructor/RadioGroup.jsx";
import FileUpload from "../../components/instructor/FileUpload.jsx";
import Header from "../../components/instructor/Header.jsx";
import { useAuth } from "../../context/AuthContext.jsx";
import { createCourse } from "../../api.js";

const STATUS_LABELS = {
    DRAFT: "Draft",
    ACTIVE: "Active",
    INACTIVE: "Inactive",
    HIDDEN: "Hidden",
};

const STATUS_OPTIONS = Object.entries(STATUS_LABELS).map(([key, label]) => ({
    value: key,
    label: label,
}));

export default function CreateCourse() {
    const navigate = useNavigate();
    const { user } = useAuth();
    const [title, setTitle] = useState("");
    const [desc, setDesc] = useState("");
    const [visibility, setVisibility] = useState("DRAFT");
    const [files, setFiles] = useState([]);
    const [previewUrl, setPreviewUrl] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        return () => {
            if (previewUrl) {
                URL.revokeObjectURL(previewUrl);
            }
        };
    }, [previewUrl]);

    const handleFilesSelected = (selectedFiles) => {
        setFiles(selectedFiles);
        if (previewUrl) URL.revokeObjectURL(previewUrl);
        if (selectedFiles[0]) {
            setPreviewUrl(URL.createObjectURL(selectedFiles[0]));
        } else {
            setPreviewUrl(null);
        }
    };

    const handleCreate = async () => {
        if (!title.trim() || !desc.trim()) {
            setError("Title and description are required.");
            return;
        }

        setIsSubmitting(true);
        setError(null);

        try {
            await createCourse(user.tenantId, {
                title: title.trim(),
                description: desc.trim(),
                instructorId: user.id,
                visibility,
            }, files[0] || null);
            navigate("/instructor/courses");
        } catch (err) {
            setError(err.response?.data?.message || "Failed to create course");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div>
            <Header
                title="Create New Course"
                description="Set up the essentials before adding content."
                showBack
                onBack={() => navigate("/instructor/courses")}
            />

            <div className="card-enterprise">
                <div className="card-enterprise-body">
                    <FormInput
                        label="Course Title"
                        placeholder="Programming with Java, etc."
                        value={title}
                        onChange={setTitle}
                    />
                    <FormInput
                        label="Description"
                        multiline
                        placeholder="Course description"
                        value={desc}
                        onChange={setDesc}
                    />
                    <FileUpload
                        label="Cover Picture"
                        onFilesSelected={handleFilesSelected}
                        accept="image/*"
                        multiple={false}
                    />
                    {previewUrl ? (
                        <div className="mb-3">
                            <img src={previewUrl} alt="Selected cover" className="img-thumbnail" style={{ width: "256px", height: "128px", objectFit: "cover" }} />
                            {files[0] && (
                                <div className="small text-muted mt-2">Selected file: {files[0].name}</div>
                            )}
                        </div>
                    ) : (
                        files[0] && (
                            <div className="small text-muted mb-3">Selected file: {files[0].name}</div>
                        )
                    )}
                    <RadioGroup
                        label="Visibility"
                        value={visibility}
                        onChange={setVisibility}
                        options={STATUS_OPTIONS}
                    />
                    <div className="alert-enterprise alert-enterprise-light">
                        You can add modules and lessons once the course is created.
                    </div>
                    {error && (
                        <div className="alert-enterprise alert-enterprise-danger">
                            {error}
                        </div>
                    )}
                    <div className="d-flex flex-wrap gap-2 mt-3">
                        <button
                            onClick={() => navigate("/instructor/courses")}
                            className="btn-enterprise btn-enterprise-secondary"
                            disabled={isSubmitting}
                        >
                            Cancel
                        </button>
                        <button
                            onClick={handleCreate}
                            className="btn-enterprise btn-enterprise-dark"
                            disabled={isSubmitting}
                        >
                            {isSubmitting ? "Creating..." : "Create Course"}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
