import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { OrderType, OrderChannel } from "@/modules/auth/types";

interface CartItem {
  productId: number;
  productName: string;
  price: number;
  quantity: number;
}

interface CartState {
  items: CartItem[];
  orderType: OrderType | null;
  channel: OrderChannel | null;
  tableNumber: string | null;
  notes: string;
}

const cartSlice = createSlice({
  name: "cart",
  initialState: {
    items: [],
    orderType: null,
    channel: null,
    tableNumber: null,
    notes: "",
  } as CartState,
  reducers: {
    setOrderContext(
      s,
      {
        payload,
      }: PayloadAction<{
        type: OrderType;
        channel: OrderChannel;
        tableNumber?: string;
      }>,
    ) {
      s.orderType = payload.type;
      s.channel = payload.channel;
      s.tableNumber = payload.tableNumber ?? null;
    },
    addItem(s, { payload }: PayloadAction<Omit<CartItem, "quantity">>) {
      const ex = s.items.find((i) => i.productId === payload.productId);
      if (ex) ex.quantity += 1;
      else s.items.push({ ...payload, quantity: 1 });
    },
    updateQuantity(
      s,
      { payload }: PayloadAction<{ productId: number; quantity: number }>,
    ) {
      const item = s.items.find((i) => i.productId === payload.productId);
      if (!item) return;
      if (payload.quantity <= 0)
        s.items = s.items.filter((i) => i.productId !== payload.productId);
      else item.quantity = payload.quantity;
    },
    clearCart: () => ({
      items: [],
      orderType: null,
      channel: null,
      tableNumber: null,
      notes: "",
    }),
    setNotes(s, { payload }: PayloadAction<string>) {
      s.notes = payload;
    },
  },
});

export const { setOrderContext, addItem, updateQuantity, clearCart, setNotes } =
  cartSlice.actions;
export default cartSlice.reducer;
