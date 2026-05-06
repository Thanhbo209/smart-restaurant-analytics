import { createBrowserRouter, Navigate } from "react-router-dom";
import { Suspense } from "react";
import { ProtectedRoute } from "@/shared/components/guards/ProtectedRoute";
import { RoleGuard } from "@/shared/components/guards/RoleGuard";
import { StaffLayout } from "@/shared/components/layouts/StaffLayout";
import { CustomerLayout } from "@/shared/components/layouts/CustomerLayout";
import { useAppSelector } from "@/store";
import type { Role } from "@/modules/auth/types";
import { PageSpinner } from "@/shared/components/ui/PageSpinner";
import { LoginPage } from "@/modules/auth/LoginPage";
import DineInMenu from "@/modules/customer/pages/DineInMenu";
import SelfServiceMenu from "@/modules/customer/pages/SelfServiceMenu";
import AdminDashboard from "@/modules/admin/pages/AdminDashboard";
import UserManagement from "@/modules/customer/pages/UserManagement";
import AnalyticsDashboard from "@/modules/manager/pages/AnalyticsDashboard";
import ProductManagement from "@/modules/manager/pages/ProductManagement";
import CashierOrderPanel from "@/modules/cashier/pages/CashierOrderPanel";
import PaymentScreen from "@/modules/cashier/pages/PaymentScreen";
import TableView from "@/modules/waiter/pages/TableView";
import KitchenBoard from "@/modules/kitchen/pages/KitchenBoard";

const W = ({ children }: { children: React.ReactNode }) => (
  <Suspense fallback={<PageSpinner />}>{children}</Suspense>
);

function RoleRedirect() {
  const role = useAppSelector((s) => s.auth.user?.role);
  const map: Record<Role, string> = {
    ADMIN: "/admin",
    MANAGER: "/manager",
    CASHIER: "/cashier",
    WAITER: "/waiter",
    KITCHEN: "/kitchen",
  };
  return <Navigate to={role ? map[role] : "/login"} replace />;
}

export const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },

  {
    path: "/customer",
    element: <CustomerLayout />,
    children: [
      {
        path: "dine-in",
        element: (
          <W>
            <DineInMenu />
          </W>
        ),
      },
      {
        path: "self-service",
        element: (
          <W>
            <SelfServiceMenu />
          </W>
        ),
      },
    ],
  },

  {
    element: <ProtectedRoute />,
    children: [
      {
        path: "/admin",
        element: (
          <RoleGuard allowed={["ADMIN"]}>
            <StaffLayout />
          </RoleGuard>
        ),
        children: [
          {
            index: true,
            element: (
              <W>
                <AdminDashboard />
              </W>
            ),
          },
          {
            path: "users",
            element: (
              <W>
                <UserManagement />
              </W>
            ),
          },
          {
            path: "analytics",
            element: (
              <W>
                <AnalyticsDashboard />
              </W>
            ),
          },
          {
            path: "products",
            element: (
              <W>
                <ProductManagement />
              </W>
            ),
          },
        ],
      },
      {
        path: "/manager",
        element: (
          <RoleGuard allowed={["MANAGER", "ADMIN"]}>
            <StaffLayout />
          </RoleGuard>
        ),
        children: [
          {
            index: true,
            element: (
              <W>
                <AnalyticsDashboard />
              </W>
            ),
          },
          {
            path: "products",
            element: (
              <W>
                <ProductManagement />
              </W>
            ),
          },
        ],
      },
      {
        path: "/cashier",
        element: (
          <RoleGuard allowed={["CASHIER", "ADMIN"]}>
            <StaffLayout />
          </RoleGuard>
        ),
        children: [
          {
            index: true,
            element: (
              <W>
                <CashierOrderPanel />
              </W>
            ),
          },
          {
            path: "payment",
            element: (
              <W>
                <PaymentScreen />
              </W>
            ),
          },
        ],
      },
      {
        path: "/waiter",
        element: (
          <RoleGuard allowed={["WAITER", "ADMIN"]}>
            <StaffLayout />
          </RoleGuard>
        ),
        children: [
          {
            index: true,
            element: (
              <W>
                <TableView />
              </W>
            ),
          },
        ],
      },
      {
        path: "/kitchen",
        element: (
          <RoleGuard allowed={["KITCHEN", "ADMIN"]}>
            <StaffLayout />
          </RoleGuard>
        ),
        children: [
          {
            index: true,
            element: (
              <W>
                <KitchenBoard />
              </W>
            ),
          },
        ],
      },
      { path: "/dashboard", element: <RoleRedirect /> },
    ],
  },

  { path: "/", element: <Navigate to="/login" replace /> },
  { path: "*", element: <Navigate to="/login" replace /> },
]);
