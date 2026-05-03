// src/components/instructor/ProfileCard.jsx
export default function ProfileCard({ name = 'Tenant Name', role = 'Instructor' }) {
    return (
        <div className="rounded-2xl border border-slate-100 bg-white p-5 shadow-sm">
            <div className="font-semibold text-slate-900">{name}</div>
            <div className="text-sm text-slate-500">{role}</div>
        </div>
    );
}
