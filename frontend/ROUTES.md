# LMS Frontend - Routes Documentation

**Version:** 1.0  
**Last Updated:** January 28, 2026

---

## Table of Contents

1. [Overview](#overview)
2. [Public Routes](#public-routes)
3. [SuperAdmin Routes](#superadmin-routes)
4. [Tenant Admin Routes](#tenant-admin-routes)
5. [Instructor Routes](#instructor-routes)
6. [Learner Routes](#learner-routes)
7. [File Structure](#file-structure)
8. [Navigation Flow](#navigation-flow)
9. [Usage Examples](#usage-examples)
10. [Implemented Features](#implemented-features)

---

## Overview

The LMS Frontend application uses **React Router** for client-side routing with comprehensive authentication guards and role-based access control.

### Route Categories

| Category | Base Path | Required Role | Description |
|----------|-----------|---------------|-------------|
| **Public Routes** | `/`, `/login` | None | Authentication pages (only accessible when not authenticated) |
| **SuperAdmin** | `/superadmin` | `SUPER_ADMIN` | Platform-level management and tenant administration |
| **Tenant Admin** | `/admin` | `ADMIN` | Organization structure and user management |
| **Instructor** | `/instructor` | `INSTRUCTOR` | Course creation, management, and learner tracking |
| **Learner** | `/learner` | `LEARNER` | Course browsing and content consumption |

### Authentication & Authorization

The application implements a multi-layered security approach:

- **ProtectedRoute Component**: Wraps all authenticated routes, enforcing role-based access control
- **PublicRoute Component**: Protects public pages, redirecting authenticated users to their dashboard
- **Role Validation**: Ensures users can only access routes permitted for their assigned role
- **Auto-Redirect**: Unauthorized access attempts automatically redirect users to their appropriate dashboard
- **JWT Authentication**: Token-based authentication with refresh token support
- **Loading States**: Displays loading indicators during authentication verification

<div style="page-break-before: always;"></div>

## Public Routes

**Access Level:** Unauthenticated users only

| Route | Component | Description |
|-------|-----------|-------------|
| `/` | `Login` | Landing page with login form |
| `/login` | `Login` | Login page (alias) |
| `*` | `Navigate` | Catch-all redirect to login |

### Protection Behavior

Public routes use the `PublicRoute` wrapper component that:

- **Blocks authenticated users** from accessing login pages
- **Redirects** authenticated users to their role-specific dashboard:

| Role | Redirect Path |
|------|---------------|
| `SUPER_ADMIN` | `/superadmin` |
| `ADMIN` | `/admin` |
| `INSTRUCTOR` | `/instructor` |
| `LEARNER` | `/learner` |

---

## SuperAdmin Routes

**Base Path:** `/superadmin`  
**Required Role:** `SUPER_ADMIN`  
**Layout Component:** `SuperAdminLayout`

### Available Routes

| Route | Component | Description |
|-------|-----------|-------------|
| `/superadmin` | `SuperAdmin` | SuperAdmin dashboard overview |
| `/superadmin/tenants` | `SuperAdmin` | View and manage all tenants |
| `/superadmin/tenants/new` | `SuperAdmin` | Create new tenant form |

### Security & Access Control

- **Protection:** All routes wrapped with `ProtectedRoute` requiring `SUPER_ADMIN` role
- **Unauthorized Access:** Users without SUPER_ADMIN role are redirected to their appropriate dashboard
- **Layout Features:**
  - Sidebar navigation
  - Logout functionality
  - Main content area

---

## Tenant Admin Routes

**Base Path:** `/admin`  
**Required Role:** `ADMIN`  
**Layout Component:** `TenantAdminLayout`

### Available Routes

| Route | Component | Description |
|-------|-----------|-------------|
| `/admin` | `Dashboard` | Admin dashboard with statistics |
| `/admin/dashboard` | `Dashboard` | Dashboard (alias route) |
| `/admin/organization` | `OrgStructure` | View organization hierarchy tree |
| `/admin/organization/create` | `CreateStructure` | Create new organizational structure |
| `/admin/organization/update` | `AddStructure` | Add items to existing structure |
| `/admin/users` | `UserManagement` | User management (CRUD operations) |

### Security & Access Control

- **Protection:** All routes wrapped with `ProtectedRoute` requiring `ADMIN` role
- **Unauthorized Access:** Users without ADMIN role are redirected to their appropriate dashboard
- **Layout Features:**
  - Sidebar navigation (Dashboard, Organization Structure, User Management)
  - Logout functionality
  - Main content area

<div style="page-break-before: always;"></div>

## Instructor Routes

**Base Path:** `/instructor`  
**Required Role:** `INSTRUCTOR`  
**Layout Component:** `InstructorLayout`

### Available Routes

| Route | Component | Description |
|-------|-----------|-------------|
| `/instructor` | `InstructorDashboard` | List of instructor's courses |
| `/instructor/courses` | `InstructorDashboard` | Courses list (alias route) |
| `/instructor/courses/new` | `CreateCourse` | Create new course form |
| `/instructor/courses/:courseId` | `CourseDetails` | View and edit course details |

### Dynamic Parameters

| Parameter | Type | Description | Example |
|-----------|------|-------------|--------|
| `:courseId` | String | Unique course identifier | `/instructor/courses/c-123` |

### Security & Access Control

- **Protection:** All routes wrapped with `ProtectedRoute` requiring `INSTRUCTOR` role
- **Unauthorized Access:** Users without INSTRUCTOR role are redirected to their appropriate dashboard
- **Layout Features:**
  - Sidebar navigation (Courses, Settings)
  - Logout functionality
  - Main content area

---

## Learner Routes

**Base Path:** `/learner`  
**Required Role:** `LEARNER`  
**Layout Component:** `LearnerLayout`

### Available Routes

| Route | Component | Description |
|-------|-----------|-------------|
| `/learner` | `Courses` | Browse available courses |
| `/learner/courses` | `Courses` | Courses list (alias route) |
| `/learner/courses/:courseId` | `CourseOverview` | Course overview and details |
| `/learner/courses/:courseId/content` | `CourseContent` | View course content and lessons |

### Dynamic Parameters

| Parameter | Type | Description | Example |
|-----------|------|-------------|--------|
| `:courseId` | String/Number | Unique course identifier | `/learner/courses/1` |

### Security & Access Control

- **Protection:** All routes wrapped with `ProtectedRoute` requiring `LEARNER` role
- **Unauthorized Access:** Users without LEARNER role are redirected to their appropriate dashboard
- **Layout Features:**
  - Sidebar navigation (Courses)
  - Logout functionality
  - Main content area

<div style="page-break-before: always;"></div>

## File Structure

The routing system is organized across multiple directories for maintainability and separation of concerns.

### Directory Organization

```
src/
├── App.jsx                    # Main router configuration with Routes/Route
├── main.jsx                   # App entry point with BrowserRouter & AuthProvider
├── login.jsx                  # Login page component
│
├── components/
│   └── ProtectedRoute.jsx     # Auth guards (ProtectedRoute & PublicRoute)
│
├── context/
│   └── AuthContext.jsx        # Authentication context & hooks
│
├── api/
│   └── authService.js         # Auth API calls & token management
│
├── layouts/
│   ├── SuperAdminLayout.jsx   # SuperAdmin layout wrapper
│   ├── TenantAdminLayout.jsx  # Tenant Admin layout wrapper
│   ├── InstructorLayout.jsx   # Instructor layout wrapper
│   └── LearnerLayout.jsx      # Learner layout wrapper
│
└── pages/
    ├── superadmin/
    │   └── SuperAdmin.jsx
    │
    ├── tenant-admin/
    │   ├── Dashboard.jsx
    │   ├── OrgStructure.jsx
    │   ├── CreateStructure.jsx
    │   ├── AddStructure.jsx
    │   └── UserManagement.jsx
    │
    ├── instructor/
    │   ├── InstructorDashboard.jsx
    │   ├── CreateCourse.jsx
    │   ├── CourseDetails.jsx
    │   └── tabs/
    │       ├── Overview.jsx
    │       ├── ContentTab.jsx
    │       └── Learners.jsx
    │
    └── learner/
        ├── Courses.jsx
        ├── CourseOverview.jsx
        └── CourseContent.jsx
```

### Key Files Description

| File/Directory | Purpose |
|----------------|----------|
| `App.jsx` | Main routing configuration using React Router |
| `main.jsx` | Application entry point with providers |
| `components/ProtectedRoute.jsx` | Authentication and authorization guards |
| `context/AuthContext.jsx` | Authentication state management |
| `api/authService.js` | Authentication API integration |
| `layouts/` | Layout wrappers for each user role |
| `pages/` | Page components organized by role |

---

## Navigation Flow

The following diagram illustrates the role-based navigation flow after successful authentication.

```
                              ┌─────────────────┐
                              │     LOGIN       │
                              │  (/ or /login)  │
                              └────────┬────────┘
                                       │
                    ┌──────────────────┼──────────────────┐
                    │                  │                  │
                    ▼                  ▼                  ▼
         ┌─────────────────┐  ┌───────────────┐  ┌────────────────┐
         │  SUPER ADMIN    │  │ TENANT ADMIN  │  │  INSTRUCTOR    │
         │  /superadmin    │  │    /admin     │  │  /instructor   │
         └────────┬────────┘  └───────┬───────┘  └────────┬───────┘
                  │                   │                   │
                  ▼                   ▼                   ▼
         ┌─────────────────┐  ┌───────────────┐  ┌────────────────┐
         │ • Manage        │  │ • Dashboard   │  │ • Courses      │
         │   Tenants       │  │ • Org         │  │ • Create       │
         │ • Create        │  │   Structure   │  │   Course       │
         │   Tenant        │  │ • Users       │  │ • Details      │
         └─────────────────┘  └───────────────┘  └────────────────┘
                  
                    │
                    ▼
         ┌─────────────────┐
         │    LEARNER      │
         │   /learner      │
         └────────┬────────┘
                  │
                  ▼
         ┌─────────────────┐
         │ • Courses       │
         │ • Overview      │
         │ • Content       │
         └─────────────────┘
```

<div style="page-break-before: always;"></div>

## Usage Examples

### Programmatic Navigation

Navigate between routes programmatically using the `useNavigate` hook.

```jsx
import { useNavigate } from 'react-router-dom';

function MyComponent() {
  const navigate = useNavigate();
  
  // Navigate to a specific route
  const goToCourses = () => {
    navigate('/instructor/courses');
  };
  
  // Navigate with dynamic parameters
  const viewCourse = (courseId) => {
    navigate(`/learner/courses/${courseId}`);
  };
  
  // Navigate with state
  const createCourse = () => {
    navigate('/instructor/courses/new', {
      state: { from: 'dashboard' }
    });
  };
  
  // Go back to previous page
  const goBack = () => {
    navigate(-1);
  };
}
```

### Accessing Route Parameters

Extract dynamic parameters from the URL using the `useParams` hook.

```jsx
import { useParams } from 'react-router-dom';

function CourseDetails() {
  const { courseId } = useParams();
  
  // Example: courseId = "c-123" when URL is /instructor/courses/c-123
  
  useEffect(() => {
    // Fetch course data using courseId
    fetchCourseData(courseId);
  }, [courseId]);
  
  return <div>Course ID: {courseId}</div>;
}
```

### Link Components

Use declarative navigation with the `Link` component.

```jsx
import { Link } from 'react-router-dom';

function Navigation() {
  return (
    <nav>
      {/* Simple navigation link */}
      <Link to="/admin/users">User Management</Link>
      
      {/* Link with dynamic parameter */}
      <Link to={`/learner/courses/${course.id}`}>
        View Course
      </Link>
      
      {/* Link with styling */}
      <Link 
        to="/instructor/courses/new"
        className="btn btn-primary"
      >
        Create New Course
      </Link>
    </nav>
  );
}
```

---

## Implemented Features

The following features have been successfully implemented in the routing system:

| Feature | Description | Status |
|---------|-------------|--------|
| **Authentication Guards** | ProtectedRoute & PublicRoute components | ✅ Complete |
| **Role-Based Access Control** | Route-level authorization by user role | ✅ Complete |
| **Auto-Redirection** | Automatic redirect based on user role | ✅ Complete |
| **Loading States** | Loading indicators during authentication | ✅ Complete |
| **JWT Authentication** | Token-based authentication system | ✅ Complete |
| **Refresh Token Support** | Automatic token refresh mechanism | ✅ Complete |
| **Catch-All Route** | Fallback redirect to login for unknown routes | ✅ Complete |

---

## Future Improvements

Planned enhancements to the routing system:

| Priority | Feature | Description |
|----------|---------|-------------|
| High | **404 Not Found Page** | Dedicated error page for invalid routes |
| High | **Route Error Boundaries** | Error handling at route level |
| Medium | **Breadcrumb Navigation** | Hierarchical navigation display |
| Medium | **Lazy Loading** | Route-based code splitting for performance |
| Low | **Route Transitions** | Animated transitions between pages |

---

**Document Version:** 1.0  
**Last Updated:** January 28, 2026  
**Maintained By:** LMS Development Team
