# Frontend-Backend Integration: Instructor Interface

## Overview
The Instructor Interface communicates with the backend API via the **Service Layer** pattern. All API calls are encapsulated in `src/api/courseService.js`. Authentication is handled automatically using Axios interceptors which inject the **JWT Access Token** from the `AuthContext`.

## Data Flow & Integration Points

### 1. Course Listing (Dashboard)
*   **Component**: `pages/instructor/InstructorDashboard.jsx`
*   **Trigger**: Component Mount (`useEffect`).
*   **Service Function**: `getCoursesByTenant(tenantId)`
*   **Backend Endpoint**: `GET /api/courses/tenants/{tenantId}`
*   **Data Handling**: Lists all courses associated with the current tenant.

### 2. Course Creation
*   **Component**: `pages/instructor/CreateCourse.jsx`
*   **Trigger**: Form Submit.
*   **Service Function**: `createCourse(tenantId, courseData, thumbnailFile)`
*   **Backend Endpoint**: `POST /api/courses`
*   **Format**: `multipart/form-data`
    *   `data`: JSON String (title, description, visibility, instructorId)
    *   `thumbnailFile`: Binary file (optional)

### 3. Course Details & Metadata Update
*   **Component**: `pages/instructor/CourseDetails.jsx` (Overview Tab)
*   **Trigger**: Component Mount (Fetch) / Save Button (Update).
*   **Service Functions**:
    *   Fetch: `getCourseById(courseId)` -> `GET /api/courses/{courseId}`
    *   Update: `updateCourse(courseId, payload, thumbnailFile)` -> `PUT /api/courses/{courseId}`
*   **Format**: Update uses `multipart/form-data` to support thumbnail updates.

### 4. Curriculum Management (Modules & Lessons)
*   **Component**: `pages/instructor/tabs/ContentTab.jsx`
*   **Service Functions** (defined in `courseService.js`):
    *   **Fetch Modules**: `getCourseModules(courseId)` -> `GET /api/courses/{courseId}/modules`
    *   **Create Module**: `createModule(courseId)` -> `POST /api/courses/{courseId}/modules`
    *   **Fetch Lessons**: `getModuleLessons(moduleId)` -> `GET /api/courses/modules/{moduleId}/lessons`
    *   **Create Lesson**: `createLesson(moduleId)` -> `POST /api/courses/modules/{moduleId}/lessons`
    *   **Update Lesson**: `updateLesson(lessonId, data)` -> `PUT /api/courses/modules/lessons/{lessonId}`

## API Service Reference (`src/api/courseService.js`)

| Function Name | Backend Endpoint | Method | Purpose |
| :--- | :--- | :--- | :--- |
| `getCoursesByTenant` | `/api/courses/tenants/{tenantId}` | `GET` | specific tenant's courses. |
| `getCourseById` | `/api/courses/{courseId}` | `GET` | Get single course details. |
| `createCourse` | `/api/courses` | `POST` | Create new course (Multipart). |
| `updateCourse` | `/api/courses/{courseId}` | `PUT` | Update course metadata (Multipart). |
| `getCourseModules` | `/api/courses/{courseId}/modules` | `GET` | List modules for a course. |
| `createModule` | `/api/courses/{courseId}/modules` | `POST` | Add a new module. |
| `getModuleLessons` | `/api/courses/modules/{moduleId}/lessons` | `GET` | List lessons in a module. |
| `createLesson` | `/api/courses/modules/{moduleId}/lessons` | `POST` | Add a new lesson. |
| `updateLesson` | `/api/courses/modules/lessons/{lessonId}` | `PUT` | Update lesson content/video. |
