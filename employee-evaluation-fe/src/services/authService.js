// src/services/authService.js
import axiosClient from "./axiosClient";

const login = async (username, password) => {
  const res = await axiosClient.post("/auth/login", { username, password });
  if (res.data.accessToken) localStorage.setItem("token", res.data.accessToken);
  if (res.data.refreshToken) localStorage.setItem("refreshToken", res.data.refreshToken);
  if (res.data.username) localStorage.setItem("username", res.data.username);
  return res.data;
};

const register = async ({ fullName, staffCode, username, email, password, department, role, level }) => {
  const res = await axiosClient.post("/auth/register", { fullName, staffCode, username, email, password, department, role, level});
  if (res.data.accessToken) localStorage.setItem("token", res.data.accessToken);
  if (res.data.refreshToken) localStorage.setItem("refreshToken", res.data.refreshToken);
  if (res.data.username) localStorage.setItem("username", res.data.username);
  return res.data;
};

const changePassword = async (oldPassword, newPassword) => {
  const res = await axiosClient.post("/auth/change-password", { oldPassword, newPassword });
  return res.data;
};


const refreshToken = async () => {
  const refresh = localStorage.getItem("refreshToken");
  if (!refresh) throw new Error("No refresh token found");
  const res = await axiosClient.post("/auth/refresh-token", { refreshToken: refresh });
  if (res.data.accessToken) localStorage.setItem("token", res.data.accessToken);
  return res.data;
};

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("username"); 
};

export default { login, register, changePassword, refreshToken, logout };
