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
  const { accessToken, user } = useAppSelector((s) => s.auth);
  const location = useLocation();

  if (!accessToken) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (!user?.role || !allowed.includes(user.role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return children ? <>{children}</> : <Outlet />;
}
