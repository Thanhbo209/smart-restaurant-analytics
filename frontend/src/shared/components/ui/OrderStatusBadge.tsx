import { Badge } from "./badge";
import { cn } from "@/shared/lib/utils";

const STATUS_CONFIG: Record<string, { label: string; className: string }> = {
  PENDING: {
    label: "Pending",
    className: "bg-yellow-100 text-yellow-800 border-yellow-200",
  },
  CONFIRMED: {
    label: "Confirmed",
    className: "bg-blue-100 text-blue-800 border-blue-200",
  },
  PREPARING: {
    label: "Preparing",
    className: "bg-orange-100 text-orange-800 border-orange-200",
  },
  READY: {
    label: "Ready",
    className: "bg-green-100 text-green-800 border-green-200",
  },
  SERVED: {
    label: "Served",
    className: "bg-teal-100 text-teal-800 border-teal-200",
  },
  OUT_FOR_DELIVERY: {
    label: "Out for delivery",
    className: "bg-purple-100 text-purple-800 border-purple-200",
  },
  DELIVERED: {
    label: "Delivered",
    className: "bg-indigo-100 text-indigo-800 border-indigo-200",
  },
  COMPLETED: {
    label: "Completed",
    className: "bg-gray-100 text-gray-700 border-gray-200",
  },
  CANCELLED: {
    label: "Cancelled",
    className: "bg-red-100 text-red-800 border-red-200",
  },
};

export function OrderStatusBadge({ status }: { status: string }) {
  const config = STATUS_CONFIG[status] ?? { label: status, className: "" };
  return (
    <Badge
      variant="outline"
      className={cn("text-xs font-medium", config.className)}
    >
      {config.label}
    </Badge>
  );
}
