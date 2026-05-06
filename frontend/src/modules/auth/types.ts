export type Role = "ADMIN" | "MANAGER" | "CASHIER" | "WAITER" | "KITCHEN";
export type OrderType = "DINE_IN" | "TAKEAWAY" | "DELIVERY";
export type OrderChannel = "IN_STORE" | "SELF_SERVICE" | "ONLINE";
export type OrderStatus =
  | "PENDING"
  | "CONFIRMED"
  | "PREPARING"
  | "READY"
  | "SERVED"
  | "OUT_FOR_DELIVERY"
  | "DELIVERED"
  | "COMPLETED"
  | "CANCELLED";
export type PaymentStatus = "UNPAID" | "PARTIALLY_PAID" | "PAID" | "REFUNDED";

export interface User {
  userId: number;
  username: string;
  fullName: string;
  role: Role;
}

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  price: number;
  quantity: number;
  subtotal: number;
}

export interface Order {
  id: number;
  type: OrderType;
  channel: OrderChannel;
  status: OrderStatus;
  paymentStatus: PaymentStatus;
  tableNumber: string | null;
  customerName: string | null;
  phone: string | null;
  address: string | null;
  totalAmount: number;
  discountAmount: number;
  finalAmount: number;
  notes: string | null;
  items: OrderItem[];
  createdAt: string;
  updatedAt: string;
}

export interface Product {
  id: number;
  name: string;
  slug: string;
  sku: string | null;
  description: string | null;
  price: number;
  cost: number | null;
  stock: number;
  imageUrl: string | null;
  isActive: boolean;
  isAvailable: boolean;
  category: { id: number; name: string; slug: string };
  createdAt: string;
  updatedAt: string;
}
