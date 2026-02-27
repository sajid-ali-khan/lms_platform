import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: '/',
});

axiosInstance.interceptors.request.use((config) => {
  try {
    const stored = localStorage.getItem('lms_user');
    if (stored) {
      const user = JSON.parse(stored);
      if (user?.accessToken) {
        config.headers['Authorization'] = `Bearer ${user.accessToken}`;
      }
    }
  } catch {
    // ignore
  }
  return config;
});

export default axiosInstance;
