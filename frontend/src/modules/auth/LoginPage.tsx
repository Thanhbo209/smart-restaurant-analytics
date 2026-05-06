import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useAppDispatch, useAppSelector } from "@/store";
import { login } from "@/store/authSlice";
import { Button } from "@/shared/components/ui/button";
import { Input } from "@/shared/components/ui/input";
import { Label } from "@/shared/components/ui/label";
import { Alert, AlertDescription } from "@/shared/components/ui/alert";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";
import type { Role } from "./types";

const schema = z.object({
  username: z.string().min(1, "Username is required"),
  password: z.string().min(1, "Password is required"),
});
type Form = z.infer<typeof schema>;

const DESTINATIONS: Record<Role, string> = {
  ADMIN: "/admin",
  MANAGER: "/manager",
  CASHIER: "/cashier",
  WAITER: "/waiter",
  KITCHEN: "/kitchen",
};

export function LoginPage() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const { isLoading, error, user } = useAppSelector((s) => s.auth);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<Form>({
    resolver: zodResolver(schema),
  });

  useEffect(() => {
    if (user?.role) {
      const from = (location.state as any)?.from?.pathname;
      navigate(from ?? DESTINATIONS[user.role], { replace: true });
    }
  }, [user]);

  const onSubmit = (values: Form) => {
    dispatch(login(values));
  };

  const sessionExpired =
    new URLSearchParams(location.search).get("reason") === "session_expired";

  return (
    <div className="min-h-screen flex items-center justify-center bg-muted/30">
      <Card className="w-full max-w-sm shadow-md">
        <CardHeader className="pb-2">
          <CardTitle className="text-2xl text-center">Staff login</CardTitle>
          <p className="text-center text-sm text-muted-foreground">
            Smart Restaurant Analytics
          </p>
        </CardHeader>
        <CardContent>
          {sessionExpired && (
            <Alert variant="destructive" className="mb-4">
              <AlertDescription>
                Your session expired. Please login again.
              </AlertDescription>
            </Alert>
          )}
          {error && (
            <Alert variant="destructive" className="mb-4">
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-1">
              <Label htmlFor="username">Username</Label>
              <Input
                id="username"
                autoComplete="username"
                {...register("username")}
              />
              {errors.username && (
                <p className="text-destructive text-xs">
                  {errors.username.message}
                </p>
              )}
            </div>

            <div className="space-y-1">
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                autoComplete="current-password"
                {...register("password")}
              />
              {errors.password && (
                <p className="text-destructive text-xs">
                  {errors.password.message}
                </p>
              )}
            </div>

            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? "Signing in..." : "Sign in"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
