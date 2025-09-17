import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080', // API Gateway
  headers: {
    'Content-Type': 'application/json',
  },
});

// Response interceptor for better error handling
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    // If API Gateway fails, try direct service (fallback)
    if (error.config && !error.config._retry && error.response?.status === 404) {
      error.config._retry = true;
      // Try direct User Service as fallback
      const originalUrl = error.config.url;
      if (originalUrl?.includes('/api/users/')) {
        error.config.baseURL = 'http://localhost:8081';
        return axiosInstance.request(error.config);
      }
    }
    return Promise.reject(error);
  }
);

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default axiosInstance;

