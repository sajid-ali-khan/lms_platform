// src/components/instructor/Tabs.jsx
export default function Tabs({ tabs, active, onChange }) {
    return (
        <div className="tabs-enterprise">
            {tabs.map((t) => (
                <button
                    key={t.value}
                    onClick={() => onChange(t.value)}
                    className={`tab-enterprise ${active === t.value ? "active" : ""}`}
                >
                    {t.label}
                </button>
            ))}
        </div>
    );
}
