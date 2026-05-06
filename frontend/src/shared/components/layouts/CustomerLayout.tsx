import { Outlet } from "react-router-dom";

export function CustomerLayout() {
  return (
    <div className="min-h-screen bg-background">
      <header className="border-b px-4 py-3">
        <span className="font-semibold text-lg">Smart Restaurant</span>
      </header>
      <main className="max-w-4xl mx-auto px-4 py-6">
        <Outlet />
      </main>
    </div>
  );
}
