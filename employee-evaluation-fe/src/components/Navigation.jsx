import '../index.css';
import { Link, useNavigate } from "react-router-dom";

function Navigation() {
    const navigate = useNavigate();

    const handleLogout = (e) => {
    e.preventDefault(); // Prevent Link's default navigation
    localStorage.removeItem("accessToken"); // Remove token
    localStorage.removeItem("username"); // Remove username
    localStorage.setItem("isLoggedIn", "false");
    navigate("/login"); // Go to login page after logout
  };
    
    return (
        <nav className="navbar">
            <a href="/" className="nav-link">Home</a>
            <a href="/employee-list" className="nav-link">Employee List</a>
            <a href='/criterion-list' className="nav-link">Criterion List</a>
            <a href="/evaluation-cycle-list" className="nav-link">Evaluation Cycles</a>
            <a href='/login' className='nav-link'>Login</a>
            <a href="/logout" className="nav-link" onClick={handleLogout}>Logout</a>
            <p>Hi, {localStorage.getItem("username") == null ? "Guest" : localStorage.getItem("username")}</p>
        </nav>
    );
}

export default Navigation;