// src/services/axiosClient.js
import axios from "axios";
import authService from "./authService";

const axiosClient = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor: tự động gắn token
axiosClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: auto refresh token khi 401
axiosClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const data = await authService.refreshToken();
        const newToken = data.accessToken;

        if (newToken) {
          originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
          return axiosClient(originalRequest);
        }
      } catch (err) {
        authService.logout();
        window.location.href = "/";
      }
    }

    return Promise.reject(error);
  }
);

export default axiosClient;

