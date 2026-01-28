# Frontend Routing Documentation

## Overview
The application uses **React Router** for client-side routing. Routes are organized by user roles, with dedicated layouts for each role.

## Route Protection
*   **PublicRoute**: Accessible only when *not* authenticated. Redirects logged-in users to their respective dashboards.
*   **ProtectedRoute**: Accessible only when authenticated and authorized. Requires specific `allowedRoles` to access.

## Public Routes
| Path | Component | Description |
| :--- | :--- | :--- |
| `/` | `Login` | The landing page (Login form). |
| `/login` | `Login` | The login page. |

## Super Admin Routes
**Base Path:** `/superadmin`
**Allowed Role:** `SUPER_ADMIN`
**Layout:** `SuperAdminLayout`

| Sub-path | Path | Component | Description |
| :--- | :--- | :--- | :--- |
| `/` | `/superadmin` | `SuperAdminDashboard` | Main dashboard for Super Admins. |
| `tenants` | `/superadmin/tenants` | `SuperAdminDashboard` | View all tenants. |
| `tenants/new` | `/superadmin/tenants/new` | `SuperAdminDashboard` | Create a new tenant. |

## Tenant Admin Routes
**Base Path:** `/admin`
**Allowed Role:** `ADMIN`
**Layout:** `TenantAdminLayout`

| Sub-path | Path | Component | Description |
| :--- | :--- | :--- | :--- |
| `/` | `/admin` | `TenantDashboard` | Main dashboard for Tenant Admins. |
| `dashboard` | `/admin/dashboard` | `TenantDashboard` | Alias for dashboard. |
| `organization` | `/admin/organization` | `OrgStructure` | View organization structure. |
| `organization/create` | `/admin/organization/create` | `CreateStructure` | Define initial organization levels. |
| `organization/update` | `/admin/organization/update` | `AddStructure` | Update/Add to org structure. |
| `users` | `/admin/users` | `UserManagement` | Manage users (create, list). |

## Instructor Routes
**Base Path:** `/instructor`
**Allowed Role:** `INSTRUCTOR`
**Layout:** `InstructorLayout`

| Sub-path | Path | Component | Description |
| :--- | :--- | :--- | :--- |
| `/` | `/instructor` | `InstructorDashboard` | Instructor's main dashboard. |
| `courses` | `/instructor/courses` | `InstructorDashboard` | View all assigned/created courses. |
| `courses/new` | `/instructor/courses/new` | `CreateCourse` | Form to create a new course. |
| `courses/:courseId` | `/instructor/courses/:courseId` | `CourseDetails` | Manage a specific course (curriculum, settings). |

## Learner Routes
**Base Path:** `/learner`
**Allowed Role:** `LEARNER`
**Layout:** `LearnerLayout`

| Sub-path | Path | Component | Description |
| :--- | :--- | :--- | :--- |
| `/` | `/learner` | `LearnerCourses` | View my enrolled courses. |
| `courses` | `/learner/courses` | `LearnerCourses` | Alias for courses. |
| `courses/:courseId` | `/learner/courses/:courseId` | `CourseOverview` | Course landing page/overview. |
| `courses/:courseId/content` | `/learner/courses/:courseId/content` | `CourseContent` | Consumption view (video player, lessons). |
