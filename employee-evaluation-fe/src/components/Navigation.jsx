import '../index.css';
import { Link, useNavigate } from "react-router-dom";
import authService from "../services/authService";

function Navigation() {
  const navigate = useNavigate();

  const handleLogout = (e) => {
    e.preventDefault();
    authService.logout();
    navigate("/");
  };

  const username = localStorage.getItem("username");

  return (
    <nav className="navbar">
      <Link to="/home" className="nav-link">Home</Link>
      <Link to="/employee-list" className="nav-link">Employee List</Link>
      <Link to="/criterion-list" className="nav-link">Criterion List</Link>
      <Link to="/evaluation-cycle-list" className="nav-link">Evaluation Cycles</Link>

      {username && (
        <div style={{ display: "flex", gap: "10px" }}>
          <span className="nav-link">Hi, {username}</span>
          <span
      onClick={() => navigate("/change-password")}
      className="nav-link"
      style={{ cursor: "pointer", color: "blue" }}
    >
      Change Password
    </span>
          <span
            onClick={handleLogout}
            className="nav-link"
            style={{ cursor: "pointer", color: "blue" }}>
            Logout
          </span>
        
        </div>
      )}
    </nav>
  );
}

export default Navigation;

