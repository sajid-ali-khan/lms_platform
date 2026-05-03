// src/components/instructor/RadioGroup.jsx
export default function RadioGroup({ label, options, value, onChange }) {
    return (
        <div className="form-group-enterprise">
            <label className="form-label-enterprise">{label}</label>
            <div className="radio-group-enterprise">
                {options.map((opt) => (
                    <label key={opt.value} className="radio-enterprise">
                        <input
                            type="radio"
                            id={`radio-${opt.value}`}
                            checked={value === opt.value}
                            onChange={() => onChange(opt.value)}
                        />
                        <span>{opt.label}</span>
                    </label>
                ))}
            </div>
        </div>
    );
}
