// src/components/instructor/InstructorSidebar.jsx
export default function InstructorSidebar({ active = 'courses', onCourses, onSettings }) {
    return (
        <aside className="w-64 bg-[#434E78] text-white min-h-screen">
            <div className="p-4 border-b border-white/20">
                <div className="font-semibold">Tenant Name</div>
                <div className="text-sm text-gray-200">Instructor</div>
            </div>

            <nav className="p-2 space-y-1">
                <button
                    onClick={onCourses}
                    className={`w-full text-left px-3 py-2 rounded ${active === 'courses' ? 'bg-white text-[#434E78] font-semibold' : 'hover:bg-white/10'
                        }`}
                >
                    Courses
                </button>
                <button
                    onClick={onSettings}
                    className={`w-full text-left px-3 py-2 rounded ${active === 'settings' ? 'bg-white text-[#434E78] font-semibold' : 'hover:bg-white/10'
                        }`}
                >
                    Settings
                </button>
            </nav>
        </aside>
    );
}
