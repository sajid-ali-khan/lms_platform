import axios from "axios";

// Use relative paths by default (works for packaged JAR)
// Can be overridden with VITE_API_URL environment variable
const API_URL = import.meta.env.VITE_API_URL || "";

const api = axios.create({
  baseURL: API_URL,
  headers: { "Content-Type": "application/json", "ngrok-skip-browser-warning": "true" },
});

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach((prom) => (error ? prom.reject(error) : prom.resolve(token)));
  failedQueue = [];
};

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      if (originalRequest.url?.includes("/auth/login") || originalRequest.url?.includes("/auth/refresh")) {
        return Promise.reject(error);
      }

      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then((token) => {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return api(originalRequest);
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      const refreshToken = localStorage.getItem("refreshToken");
      if (!refreshToken) {
        localStorage.clear();
        window.location.href = "/login";
        return Promise.reject(error);
      }

      try {
        const { data } = await axios.post(`/api/auth/refresh`, { refreshToken });
        localStorage.setItem("accessToken", data.accessToken);
        if (data.refreshToken) localStorage.setItem("refreshToken", data.refreshToken);
        
        api.defaults.headers.common.Authorization = `Bearer ${data.accessToken}`;
        originalRequest.headers.Authorization = `Bearer ${data.accessToken}`;
        processQueue(null, data.accessToken);
        return api(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        localStorage.clear();
        window.location.href = "/login";
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }
    return Promise.reject(error);
  }
);

export const login = async (username, password) => {
  const { data } = await api.post("/api/auth/login", { username, password });
  localStorage.setItem("accessToken", data.accessToken);
  localStorage.setItem("refreshToken", data.refreshToken);
  localStorage.setItem("user", JSON.stringify({
    id: data.userId,
    email: data.userEmail,
    fullName: data.fullName,
    role: data.role,
    tenantId: data.tenantId,
  }));
  return data;
};

export const logout = async () => {
  const refreshToken = localStorage.getItem("refreshToken");
  if (refreshToken) {
    try {
      await api.post("/api/auth/logout", { refreshToken });
    } catch (e) {}
  }
  localStorage.clear();
};

export const getUser = () => {
  const userStr = localStorage.getItem("user");
  return userStr ? JSON.parse(userStr) : null;
};

export const getCourses = async (tenantId) => {
  const { data } = await api.get(`/api/courses/tenants/${tenantId}`);
  if (Array.isArray(data)) return data;
  if (typeof data === 'object' && data !== null) {
    return Object.entries(data).map(([id, course]) => ({ ...course, id }));
  }
  return [];
};

export const getCourse = async (courseId) => {
  const { data } = await api.get(`/api/courses/${courseId}`);
  return data;
};

export const deleteCourse = async (courseId) => {
  await api.delete(`/api/courses/${courseId}`);
};

// ─── Super Admin ─────────────────────────────────────────────────────────────

export const getTenants = async () => {
  const { data } = await api.get("/api/tenants");
  return data;
};

export const createTenant = async ({ tenantName, tenantCategory, adminFullName, adminEmail }) => {
  const { data } = await api.post("/api/tenants", {
    tenantName,
    tenantCategory,
    adminFullName,
    adminEmail,
  });
  return data; // { tenantName, tenantCategory, adminEmail, adminPassword }
};

// ─── Tenant Admin ─────────────────────────────────────────────────────────────

export const getDashboard = async (tenantId) => {
  const { data } = await api.get(`/api/tenants/${tenantId}/admin/dashboard`);
  return data; // { userCount, instructorCount, learnerCount, courseCount, activeCourseCount, orgUnitsCountMaps }
};

export const getUsers = async (tenantId) => {
  const { data } = await api.get(`/api/tenants/${tenantId}/users`);
  return data; // [{ id, username, fullName, email, role }]
};

export const createUser = async (tenantId, { fullName, email, password, role }) => {
  const { data } = await api.post(`/api/tenants/${tenantId}/users`, { fullName, email, password, role });
  return data;
};

export const updateUser = async (tenantId, userId, { fullName, email, password }) => {
  const { data } = await api.put(`/api/tenants/${tenantId}/users/${userId}`, { fullName, email, password });
  return data;
};

export const deleteUser = async (tenantId, userId) => {
  await api.delete(`/api/tenants/${tenantId}/users/${userId}`);
};

export const getOrgStructures = async (tenantId) => {
  const { data } = await api.get(`/api/tenants/${tenantId}/org-structures`);
  return data; // [{ id, name, structure: string[] }]
};

export const getOrgStructuresDetailed = async (tenantId) => {
  const { data } = await api.get(`/api/tenants/${tenantId}/org-structures/detailed`);
  return data; // [{ id, name, structure: [{ id, name, level, parentName }] }]
};

export const createOrgStructure = async (tenantId, { name, hierarchyLevels }) => {
  await api.post(`/api/tenants/${tenantId}/org-structures`, { name, hierarchyLevels });
};

export const getOrgUnitsTree = async (tenantId, structureId) => {
  const { data } = await api.get(`/api/tenants/${tenantId}/org-units/structure/${structureId}/tree`);
  return data; // [{ id, name, level, parentId }]
};

export const createOrgUnit = async (tenantId, { orgUnitTypeId, name, parentOrgUnitId, attributes }) => {
  await api.post(`/api/tenants/${tenantId}/org-units`, {
    orgUnitTypeId,
    name,
    parentOrgUnitId: parentOrgUnitId || null,
    attributes: attributes || {},
  });
};

export const getOrgUnitDetails = async (tenantId, orgUnitId) => {
  const { data } = await api.get(`/api/tenants/${tenantId}/org-units/${orgUnitId}/details`);
  return data;
};

export const createCourse = async (tenantId, courseData, thumbnailFile) => {
  const formData = new FormData();
  formData.append("tenantId", tenantId);
  if (thumbnailFile) formData.append("thumbnailFile", thumbnailFile);
  formData.append("data", new Blob([JSON.stringify(courseData)], { type: "application/json" }));
  
  const { data } = await api.post("/api/courses", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return data;
};

export const updateCourse = async (courseId, updateData, thumbnailFile = null) => {
  const formData = new FormData();
  formData.append("data", new Blob([JSON.stringify(updateData)], { type: "application/json" }));
  if (thumbnailFile) formData.append("thumbnailFile", thumbnailFile);
  
  await api.put(`/api/courses/${courseId}`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

export const getModules = async (courseId) => {
  const { data } = await api.get(`/api/modules/courses/${courseId}`);
  return data;
};

export const createModule = async (courseId) => {
  await api.post(`/api/modules/courses/${courseId}`);
};

export const getLessons = async (moduleId) => {
  const { data } = await api.get(`/api/lessons/modules/${moduleId}`);
  return data;
};

export const createLesson = async (moduleId) => {
  await api.post(`/api/lessons/modules/${moduleId}`);
};

export const updateLesson = async (lessonId, lessonData) => {
  await api.put(`/api/lessons/${lessonId}`, lessonData);
};

export const getEnrollmentsByCourse = async (courseId) => {
  const { data } = await api.get(`/api/enrollments/courses/${courseId}`);
  return data; // [{ id, learnerId, learnerName, learnerEmail, orgUnitPath, status, enrolledAt }]
};

// ─── Learner ─────────────────────────────────────────────────────────────────

export const getEnrolledCourses = async (learnerId) => {
  const { data } = await api.get(`/api/learners/${learnerId}/courses`);
  if (Array.isArray(data)) return data;
  if (typeof data === 'object' && data !== null) {
    return Object.entries(data).map(([id, course]) => ({ ...course, id }));
  }
  return [];
};

export const enrollInCourse = async (courseId, learnerId) => {
  await api.post('/api/enrollments', { courseId, learnerId });
};

// ─── User Org Unit Assignments ───────────────────────────────────────────────

export const getUserOrgUnits = async (tenantId, userId) => {
  const { data } = await api.get(`/api/tenants/${tenantId}/users/${userId}/org-units`);
  return data; // [{ id, orgUnitId, orgUnitName, isPrimary }]
};

export const assignUserOrgUnit = async (tenantId, userId, { orgUnitId, isPrimary }) => {
  const { data } = await api.post(`/api/tenants/${tenantId}/users/${userId}/org-units`, { orgUnitId, isPrimary });
  return data;
};

export const removeUserOrgUnit = async (tenantId, userId, orgUnitId) => {
  await api.delete(`/api/tenants/${tenantId}/users/${userId}/org-units/${orgUnitId}`);
};

// ─── Course Allocations ──────────────────────────────────────────────────────

export const getCourseAllocations = async (courseId) => {
  const { data } = await api.get(`/api/courses/${courseId}/allocations`);
  return data; // [{ id, orgUnitId, orgUnitName, isMandatory }]
};

export const allocateCourse = async (courseId, { orgUnitId, isMandatory }) => {
  const { data } = await api.post(`/api/courses/${courseId}/allocations`, { orgUnitId, isMandatory });
  return data;
};

export const removeCourseAllocation = async (courseId, orgUnitId) => {
  await api.delete(`/api/courses/${courseId}/allocations/${orgUnitId}`);
};

// ─── Learner Available Courses ───────────────────────────────────────────────

export const getAvailableCourses = async (learnerId) => {
  const { data } = await api.get(`/api/learners/${learnerId}/available-courses`);
  return Array.isArray(data) ? data : [];
};

// ─── Change Password ─────────────────────────────────────────────────────────

export const changePassword = async (currentPassword, newPassword) => {
  const { data } = await api.put("/api/auth/change-password", { currentPassword, newPassword });
  return data;
};

export default api;
