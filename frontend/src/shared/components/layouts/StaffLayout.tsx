import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "@/store";
import { logout } from "@/store/authSlice";
import { Badge } from "../ui/badge";
import { Button } from "../ui/button";
import {
  LayoutDashboard,
  ShoppingCart,
  CreditCard,
  UtensilsCrossed,
  ChefHat,
  BarChart3,
  Package,
  Users,
  LogOut,
} from "lucide-react";
import type { Role } from "@/modules/auth/types";

const NAV: Record<
  Role,
  { to: string; label: string; icon: React.ElementType }[]
> = {
  ADMIN: [
    { to: "/admin", label: "Dashboard", icon: LayoutDashboard },
    { to: "/admin/users", label: "Users", icon: Users },
    { to: "/admin/analytics", label: "Analytics", icon: BarChart3 },
    { to: "/admin/products", label: "Products", icon: Package },
    { to: "/admin/orders", label: "Orders", icon: ShoppingCart },
  ],
  MANAGER: [
    { to: "/manager", label: "Analytics", icon: BarChart3 },
    { to: "/manager/products", label: "Products", icon: Package },
    { to: "/manager/orders", label: "Orders", icon: ShoppingCart },
  ],
  CASHIER: [
    { to: "/cashier", label: "New order", icon: ShoppingCart },
    { to: "/cashier/payment", label: "Payment", icon: CreditCard },
    { to: "/cashier/queue", label: "Queue", icon: LayoutDashboard },
  ],
  WAITER: [{ to: "/waiter", label: "Tables", icon: UtensilsCrossed }],
  KITCHEN: [{ to: "/kitchen", label: "Orders", icon: ChefHat }],
};

export function StaffLayout() {
  const { user } = useAppSelector((s) => s.auth);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const navItems = NAV[user?.role ?? "CASHIER"] ?? [];

  const handleLogout = async () => {
    await dispatch(logout());
    navigate("/login");
  };

  return (
    <div className="flex h-screen bg-background">
      <aside className="w-56 border-r flex flex-col py-4 px-3 gap-1 shrink-0">
        <div className="px-2 py-3 mb-2 border-b">
          <p className="text-sm font-medium truncate">{user?.fullName}</p>
          <Badge variant="secondary" className="mt-1 text-xs">
            {user?.role}
          </Badge>
        </div>

        <nav className="flex-1 flex flex-col gap-1">
          {navItems.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              end
              className={({ isActive }) =>
                `flex items-center gap-2 px-3 py-2 rounded-md text-sm transition-colors
               ${
                 isActive
                   ? "bg-primary text-primary-foreground"
                   : "text-muted-foreground hover:bg-accent hover:text-accent-foreground"
               }`
              }
            >
              <Icon size={16} />
              {label}
            </NavLink>
          ))}
        </nav>

        <Button
          variant="ghost"
          size="sm"
          onClick={handleLogout}
          className="justify-start gap-2 text-muted-foreground"
        >
          <LogOut size={16} />
          Logout
        </Button>
      </aside>

      <main className="flex-1 overflow-auto p-6">
        <Outlet />
      </main>
    </div>
  );
}
