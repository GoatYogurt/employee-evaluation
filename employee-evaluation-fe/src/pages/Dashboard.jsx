import React from "react";
import Navigation from "../components/Navigation";
import "../index.css";
import "./dashboard.css";

function Dashboard({ children }) {
  return (
    <div className="dashboard">
      {/* Bên trái: Sidebar */}
      <aside className="sidebar">
        <Navigation />
      </aside>

      {/* Bên phải: Nội dung chính */}
      <main className="main-content">
        {children}
      </main>
    </div>
  );
}

export default Dashboard;
