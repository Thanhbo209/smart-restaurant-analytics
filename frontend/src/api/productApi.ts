import api from "./axiosInstance";
export const productApi = {
  getAll: (params?: object) => api.get("/products", { params }),
  getById: (id: number) => api.get(`/products/${id}`),
  getBySlug: (slug: string) => api.get(`/products/by-slug/${slug}`),
  create: (body: unknown) => api.post("/products", body),
  update: (id: number, body: unknown) => api.put(`/products/${id}`, body),
  toggleAvailability: (id: number, body: { isAvailable: boolean }) =>
    api.patch(`/products/${id}/availability`, body),
};
