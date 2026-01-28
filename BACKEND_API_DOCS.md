# Backend Web API Documentation

## 1. Entities

*   **Tenant**: Represents a top-level organization or customer using the platform (Multi-tenancy root).
*   **User**: A system user with a specific role (Super Admin, Tenant Admin, Instructor, Learner).
*   **Course**: A container for educational content, including modules and lessons.
*   **Module**: A section or chapter within a course.
*   **Lesson**: A specific unit of learning content (video, text, etc.) within a module.
*   **CourseAllocation**: Represents the assignment of a course to an Organizational Unit, potentially mandatory.
*   **Enrollment**: Tracks a user's registration and participation in a specific course.
*   **OrgStructure**: Defines the hierarchical structure layout for a tenant.
*   **OrgUnit**: A specific department, team, or division within an organization's structure.
*   **OrgUnitType**: Defines the type/level of an organizational unit (e.g., Division, Department).
*   **FileResource**: Represents metadata for a file uploaded to the system (images, videos, docs).
*   **LessonProgress**: Tracks the completion status of a specific lesson for a user.
*   **RefreshToken**: A token used to obtain a new access token when the current one expires.

## 2. Authentication and Authorization

*   **Authentication**: Implemented using **Spring Security** with **JSON Web Tokens (JWT)**.
    *   The system is **stateless**.
    *   Clients authenticate via the `/api/auth/login` endpoint to receive an Access Token and a Refresh Token.
    *   The Access Token must be included in the `Authorization` header (`Bearer <token>`) for protected endpoints.
*   **Authorization**: Implemented using **Role-Based Access Control (RBAC)**.
    *   Users are assigned roles such as `ROLE_SUPER_ADMIN`, `ROLE_TENANT_ADMIN`, `ROLE_INSTRUCTOR`, or `ROLE_LEARNER`.
    *   Access to specific endpoints is restricted based on these roles using method-level security.

## 3. API Endpoints

### Authentication
| Method | Path | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Authenticate a user and return JWT tokens. |
| `POST` | `/api/auth/refresh` | Refresh an expired access token using a valid refresh token. |
| `POST` | `/api/auth/logout` | Logout a user and invalidate the refresh token. |

### Tenants
| Method | Path | Description |
| :--- | :--- | :--- |
| `GET` | `/api/tenants` | Retrieve a list of all tenants. |
| `POST` | `/api/tenants` | Create a new tenant along with an initial admin user. |

### Users
| Method | Path | Description |
| :--- | :--- | :--- |
| `POST` | `/api/tenants/{tenantId}/users` | Create a new user within a specific tenant. |
| `GET` | `/api/tenants/{tenantId}/users` | Retrieve all users belonging to a specific tenant. |
| `GET` | `/api/tenants/{tenantId}/users/{id}` | Retrieve details of a specific user by ID. |

### Courses
| Method | Path | Description |
| :--- | :--- | :--- |
| `GET` | `/api/courses/tenants/{tenantId}` | Get all courses available for a tenant. |
| `GET` | `/api/courses/{courseId}` | Get details of a specific course. |
| `POST` | `/api/courses` | Create a new course (Multipart resource). |
| `PUT` | `/api/courses/{courseId}` | Update an existing course (Multipart resource). |
| `POST` | `/api/courses/{courseId}/modules` | Add a new module to a course. |
| `GET` | `/api/courses/{courseId}/modules` | Get all modules for a course. |
| `POST` | `/api/courses/modules/{moduleId}/lessons` | Add a new lesson to a module. |
| `GET` | `/api/courses/modules/{moduleId}/lessons` | Get all lessons for a module. |
| `PUT` | `/api/courses/modules/lessons/{lessonId}` | Update a lesson's content or details. |

### Organization Structure
| Method | Path | Description |
| :--- | :--- | :--- |
| `POST` | `/api/tenants/{tenantId}/org-structures` | Create a new organization structure definition. |
| `GET` | `/api/tenants/{tenantId}/org-structures` | Get basic info of tenant structures. |
| `GET` | `/api/tenants/{tenantId}/org-structures/detailed` | Get detailed tenant structures. |

### Organization Units
| Method | Path | Description |
| :--- | :--- | :--- |
| `POST` | `/api/tenants/{tenantId}/org-units` | Create a new organizational unit. |
| `GET` | `/api/tenants/{tenantId}/org-units` | Retrieve org units filtered by structure, type, or parent. |
| `GET` | `/api/tenants/{tenantId}/org-units/structure/{structureId}/tree` | Get the full tree hierarchy of org units for a structure. |
| `GET` | `/api/tenants/{tenantId}/org-units/{orgUnitId}/details` | Get details of a specific organizational unit. |

### Instructors
| Method | Path | Description |
| :--- | :--- | :--- |
| `GET` | `/api/instructors/{instructorId}/courses` | Get courses assigned to or created by a specific instructor. |

### Resources (Files)
| Method | Path | Description |
| :--- | :--- | :--- |
| `GET` | `/api/resources/{resourceId}` | Retrieve/Download a stored file resource. |

### Dashboard
| Method | Path | Description |
| :--- | :--- | :--- |
| `GET` | `/api/tenants/{tenantId}/admin/dashboard` | Get dashboard statistics and details for tenant verification. |
