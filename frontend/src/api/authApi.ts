import api from "./axiosInstance";
export const authApi = {
  login: (b: { username: string; password: string; deviceId: string }) =>
    api.post("/auth/login", b),
  refresh: (b: { refreshToken: string; deviceId: string }) =>
    api.post("/auth/refresh", b),
  logout: (b: { refreshToken: string; deviceId: string }) =>
    api.post("/auth/logout", b),
  me: () => api.get("/auth/me"),
};
