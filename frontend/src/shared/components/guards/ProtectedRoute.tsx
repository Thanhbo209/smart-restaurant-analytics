import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAppSelector } from "@/store";
import { PageSpinner } from "@/shared/components/ui/PageSpinner";

export function ProtectedRoute() {
  const { accessToken, isLoading } = useAppSelector((s) => s.auth);
  const location = useLocation();
  if (isLoading) return <PageSpinner />;
  if (!accessToken)
    return <Navigate to="/login" state={{ from: location }} replace />;
  return <Outlet />;
}
