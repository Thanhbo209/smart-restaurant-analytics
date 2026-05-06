import axios from "axios";
import { store } from "@/store";
import { refreshTokens, clearAuth } from "@/store/authSlice";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? "/api/v1",
  timeout: 15_000,
});

api.interceptors.request.use((config) => {
  const token = store.getState().auth.accessToken;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

let isRefreshing = false;
let queue: Array<{
  resolve: (t: string) => void;
  reject: (e: unknown) => void;
}> = [];

const flush = (err: unknown, token: string | null) => {
  queue.forEach((p) => (err ? p.reject(err) : p.resolve(token!)));
  queue = [];
};

api.interceptors.response.use(
  (r) => r,
  async (error) => {
    const orig = error.config;
    if (error.response?.status !== 401 || orig._retry)
      return Promise.reject(error);

    if (isRefreshing) {
      return new Promise((resolve, reject) =>
        queue.push({ resolve, reject }),
      ).then((token) => {
        orig.headers.Authorization = `Bearer ${token}`;
        return api(orig);
      });
    }

    orig._retry = true;
    isRefreshing = true;

    try {
      const res = await store.dispatch(refreshTokens()).unwrap();
      flush(null, res.accessToken);
      orig.headers.Authorization = `Bearer ${res.accessToken}`;
      return api(orig);
    } catch (e) {
      flush(e, null);
      store.dispatch(clearAuth());
      window.location.href = "/login?reason=session_expired";
      return Promise.reject(e);
    } finally {
      isRefreshing = false;
    }
  },
);

export default api;
