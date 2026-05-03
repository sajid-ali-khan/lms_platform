// src/App.jsx
import { Routes, Route, Navigate } from "react-router-dom";
import ProtectedRoute, { PublicRoute } from "./components/ProtectedRoute";

// Layouts
import TenantAdminLayout from "./layouts/TenantAdminLayout";
import SuperAdminLayout from "./layouts/SuperAdminLayout";
import InstructorLayout from "./layouts/InstructorLayout";
import LearnerLayout from "./layouts/LearnerLayout";

// Auth
import Login from "./login";

// SuperAdmin Pages
import SuperAdminDashboard from "./pages/superadmin/SuperAdmin";
import SuperAdminSettings from "./pages/superadmin/Settings";

// Tenant Admin Pages
import TenantDashboard from "./pages/tenant-admin/Dashboard";
import OrgStructure from "./pages/tenant-admin/OrgStructure";
import CreateStructure from "./pages/tenant-admin/CreateStructure";
import AddStructure from "./pages/tenant-admin/AddStructure";
import UserManagement from "./pages/tenant-admin/UserManagement";
import TenantAdminSettings from "./pages/tenant-admin/Settings";

// Instructor Pages
import InstructorDashboard from "./pages/instructor/InstructorDashboard";
import CreateCourse from "./pages/instructor/CreateCourse";
import CourseDetails from "./pages/instructor/CourseDetails";
import Inactive from "./pages/instructor/Inactive";
import Drafts from "./pages/instructor/Drafts";
import Hidden from "./pages/instructor/Hidden";
import Enrollments from "./pages/instructor/Enrollments";
import Stats from "./pages/instructor/Stats";
import Settings from "./pages/instructor/Settings";

// Learner Pages
import LearnerCourses from "./pages/learner/Courses";
import CourseOverview from "./pages/learner/CourseOverview";
import CourseContent from "./pages/learner/CourseContent";
import LearnerSettings from "./pages/learner/Settings";

export default function App() {
  return (
    <Routes>
      {/* Public Routes */}
      <Route
        path="/"
        element={
          <PublicRoute>
            <Login />
          </PublicRoute>
        }
      />
      <Route
        path="/login"
        element={
          <PublicRoute>
            <Login />
          </PublicRoute>
        }
      />

      {/* Super Admin Routes */}
      <Route
        path="/superadmin"
        element={
          <ProtectedRoute allowedRoles={["SUPER_ADMIN"]}>
            <SuperAdminLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<SuperAdminDashboard />} />
        <Route path="tenants" element={<SuperAdminDashboard />} />
        <Route path="tenants/new" element={<SuperAdminDashboard />} />
        <Route path="settings" element={<SuperAdminSettings />} />
      </Route>

      {/* Tenant Admin Routes */}
      <Route
        path="/admin"
        element={
          <ProtectedRoute allowedRoles={["ADMIN"]}>
            <TenantAdminLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<TenantDashboard />} />
        <Route path="dashboard" element={<TenantDashboard />} />
        <Route path="organization" element={<OrgStructure />} />
        <Route path="organization/create" element={<CreateStructure />} />
        <Route path="organization/update" element={<AddStructure />} />
        <Route path="users" element={<UserManagement />} />
        <Route path="settings" element={<TenantAdminSettings />} />
      </Route>

      {/* Instructor Routes */}
      <Route
        path="/instructor"
        element={
          <ProtectedRoute allowedRoles={["INSTRUCTOR"]}>
            <InstructorLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<InstructorDashboard />} />
        <Route path="courses" element={<InstructorDashboard />} />
        <Route path="courses/new" element={<CreateCourse />} />
        <Route path="courses/:courseId" element={<CourseDetails />} />
        <Route path="drafts" element={<Drafts />} />
        <Route path="inactive" element={<Inactive />} />
        <Route path="hidden" element={<Hidden />} />
        <Route path="enrollments" element={<Enrollments />} />
        <Route path="stats" element={<Stats />} />
        <Route path="settings" element={<Settings />} />
      </Route>

      {/* Learner Routes */}
      <Route
        path="/learner"
        element={
          <ProtectedRoute allowedRoles={["LEARNER"]}>
            <LearnerLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<LearnerCourses />} />
        <Route path="courses" element={<LearnerCourses />} />
        <Route path="courses/:courseId" element={<CourseOverview />} />
        <Route path="courses/:courseId/content" element={<CourseContent />} />
        <Route path="settings" element={<LearnerSettings />} />
      </Route>

      {/* Catch all - redirect to login */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
