import api from "./axiosInstance";
export const userApi = {
  getAll: (params?: object) => api.get("/users", { params }),
  create: (body: unknown) => api.post("/users", body),
  update: (id: number, b: unknown) => api.put(`/users/${id}`, b),
  changePassword: (id: number, b: { newPassword: string }) =>
    api.patch(`/users/${id}/password`, b),
  enable: (id: number) => api.patch(`/users/${id}/enable`),
  disable: (id: number) => api.patch(`/users/${id}/disable`),
  delete: (id: number) => api.delete(`/users/${id}`),
};
