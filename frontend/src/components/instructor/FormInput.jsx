// src/components/instructor/FormInput.jsx
export default function FormInput({ label, placeholder, value, onChange, multiline = false, disabled = false }) {
    return (
        <div className="form-group-enterprise">
            <label className="form-label-enterprise">{label}</label>
            {multiline ? (
                <textarea
                    className="form-control-enterprise"
                    rows={4}
                    placeholder={placeholder}
                    value={value}
                    onChange={(e) => onChange(e.target.value)}
                    disabled={disabled}
                />
            ) : (
                <input
                    className="form-control-enterprise"
                    placeholder={placeholder}
                    value={value}
                    onChange={(e) => onChange(e.target.value)}
                    disabled={disabled}
                />
            )}
        </div>
    );
}
