import axios from 'axios';

// Resolve backend API URL intelligently
let backendUrl = import.meta.env.VITE_API_URL;

if (!backendUrl) {
  // If not set via env var, check if running in production or development
  if (typeof window !== 'undefined' && window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1') {
    backendUrl = 'https://medislot-1ecf.onrender.com/api';
  } else {
    backendUrl = '/api';
  }
}

// Clean up URL: remove trailing slash
backendUrl = backendUrl.trim().replace(/\/+$/, '');

// Ensure it ends with /api if it's pointing to the root domain
if (!backendUrl.endsWith('/api') && backendUrl.startsWith('http')) {
  backendUrl = `${backendUrl}/api`;
}

console.log('[MediSlot API] Using Base URL:', backendUrl);

const api = axios.create({
  baseURL: backendUrl,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor to inject JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('medislot_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor to handle unauthorized responses
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Clear token if expired or invalid
      if (!window.location.pathname.includes('/login') && window.location.pathname !== '/') {
        localStorage.removeItem('medislot_token');
        localStorage.removeItem('medislot_user');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
