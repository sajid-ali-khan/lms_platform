// src/components/instructor/FileUpload.jsx
import { useRef } from "react";

export default function FileUpload({
    label,
    onFilesSelected,
    accept = "*/*",
    multiple = false,
}) {
    const inputRef = useRef(null);

    const handleChange = (event) => {
        const selectedFiles = Array.from(event.target.files || []);
        onFilesSelected(selectedFiles);
    };

    const handleClick = () => {
        inputRef.current?.click();
    };

    return (
        <div className="form-group-enterprise">
            <label className="form-label-enterprise">{label}</label>
            <div onClick={handleClick} className="file-upload-enterprise">
                <svg
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 24 24"
                    className="file-upload-enterprise-icon"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth={1.5}
                >
                    <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M3 16.5v2.25A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75V16.5M16.5 9L12 4.5 7.5 9M12 4.5v12"
                    />
                </svg>
                <p className="file-upload-enterprise-text">Click to upload</p>
                <p className="file-upload-enterprise-hint">or drag & drop files</p>
                <input
                    ref={inputRef}
                    type="file"
                    accept={accept}
                    multiple={multiple}
                    style={{ display: "none" }}
                    onChange={handleChange}
                />
            </div>
        </div>
    );
}
