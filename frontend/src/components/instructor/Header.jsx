// src/components/instructor/Header.jsx
export default function Header({ title, description, actions, showBack = false, onBack }) {
    return (
        <div className="page-header">
            <div className="page-header-left">
                {showBack && (
                    <button
                        onClick={onBack}
                        className="page-header-back"
                        aria-label="Go back"
                    >
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="16"
                            height="16"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                            strokeWidth={2}
                        >
                            <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
                        </svg>
                        Back
                    </button>
                )}
                <div>
                    <h1 className="page-header-title">{title}</h1>
                    {description && <p className="page-header-desc">{description}</p>}
                </div>
            </div>
            {actions && <div className="page-header-actions">{actions}</div>}
        </div>
    );
}
