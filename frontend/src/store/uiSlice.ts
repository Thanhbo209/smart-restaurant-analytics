import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

interface UiState {
  isLoading: boolean;
}

const uiSlice = createSlice({
  name: "ui",
  initialState: { isLoading: false } as UiState,
  reducers: {
    setLoading(s, { payload }: PayloadAction<boolean>) {
      s.isLoading = payload;
    },
  },
});

export const { setLoading } = uiSlice.actions;
export default uiSlice.reducer;
