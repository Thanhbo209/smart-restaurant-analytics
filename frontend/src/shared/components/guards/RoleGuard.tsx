import { Navigate, Outlet } from "react-router-dom";
import { useAppSelector } from "@/store";
import type { Role } from "@/modules/auth/types";

export function RoleGuard({
  allowed,
  children,
}: {
  allowed: Role[];
  children?: React.ReactNode;
}) {
  const role = useAppSelector((s) => s.auth.user?.role);
  if (!role || !allowed.includes(role)) return <Navigate to="/login" replace />;
  return children ? <>{children}</> : <Outlet />;
}
