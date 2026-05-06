import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import type { User } from "@/modules/auth/types";
import { authApi } from "@/api/authApi";

interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  deviceId: string;
  isLoading: boolean;
  error: string | null;
}

const deviceId = localStorage.getItem("deviceId") ?? crypto.randomUUID();
localStorage.setItem("deviceId", deviceId);

const initialState: AuthState = {
  user: null,
  accessToken: null,
  refreshToken: localStorage.getItem("refreshToken"),
  deviceId,
  isLoading: false,
  error: null,
};

export const login = createAsyncThunk(
  "auth/login",
  async (
    credentials: { username: string; password: string },
    { rejectWithValue },
  ) => {
    try {
      const res = await authApi.login({ ...credentials, deviceId });
      return res.data.data;
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      return rejectWithValue(error.response?.data?.message ?? "Login failed");
    }
  },
);

export const refreshTokens = createAsyncThunk(
  "auth/refresh",
  async (_, { getState, rejectWithValue }) => {
    const { auth } = getState() as { auth: AuthState };
    if (!auth.refreshToken) return rejectWithValue("No refresh token");
    try {
      const res = await authApi.refresh({
        refreshToken: auth.refreshToken,
        deviceId: auth.deviceId,
      });
      return res.data.data;
    } catch {
      return rejectWithValue("Session expired");
    }
  },
);

export const logout = createAsyncThunk(
  "auth/logout",
  async (_, { getState }) => {
    const { auth } = getState() as { auth: AuthState };
    if (auth.refreshToken) {
      await authApi
        .logout({ refreshToken: auth.refreshToken, deviceId: auth.deviceId })
        .catch(() => {});
    }
  },
);

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    clearAuth(state) {
      state.user = null;
      state.accessToken = null;
      state.refreshToken = null;
      localStorage.removeItem("refreshToken");
    },
  },
  extraReducers: (builder) =>
    builder
      .addCase(login.pending, (s) => {
        s.isLoading = true;
        s.error = null;
      })
      .addCase(login.fulfilled, (s, { payload }) => {
        s.isLoading = false;
        s.error = null;
        s.user = {
          userId: payload.userId,
          username: payload.username,
          fullName: payload.fullName,
          role: payload.role,
        };
        s.accessToken = payload.accessToken;
        s.refreshToken = payload.refreshToken;
        localStorage.setItem("refreshToken", payload.refreshToken);
      })
      .addCase(login.rejected, (s, { payload }) => {
        s.isLoading = false;
        s.error = payload as string;
      })
      .addCase(refreshTokens.fulfilled, (s, { payload }) => {
        s.accessToken = payload.accessToken;
        s.refreshToken = payload.refreshToken;
        localStorage.setItem("refreshToken", payload.refreshToken);
      })
      .addCase(refreshTokens.rejected, (s) => {
        s.user = null;
        s.accessToken = null;
        s.refreshToken = null;
        localStorage.removeItem("refreshToken");
      })
      .addCase(logout.fulfilled, (s) => {
        s.user = null;
        s.accessToken = null;
        s.refreshToken = null;
        localStorage.removeItem("refreshToken");
      }),
});

export const { clearAuth } = authSlice.actions;
export default authSlice.reducer;
