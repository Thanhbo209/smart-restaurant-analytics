import api from "./axiosInstance";
export const orderApi = {
  create: (body: unknown) => api.post("/orders", body),
  getById: (id: number) => api.get(`/orders/${id}`),
  getOrders: (params: object) => api.get("/orders", { params }),
  confirm: (id: number) => api.post(`/orders/${id}/confirm`),
  startPreparing: (id: number) => api.post(`/orders/${id}/start-preparing`),
  markReady: (id: number) => api.post(`/orders/${id}/ready`),
  serve: (id: number) => api.post(`/orders/${id}/serve`),
  outForDelivery: (id: number) => api.post(`/orders/${id}/out-for-delivery`),
  deliver: (id: number) => api.post(`/orders/${id}/deliver`),
  cancel: (id: number) => api.post(`/orders/${id}/cancel`),
  pay: (id: number, b: unknown) => api.post(`/orders/${id}/pay`, b),
  getPayments: (id: number) => api.get(`/orders/${id}/payments`),
};
