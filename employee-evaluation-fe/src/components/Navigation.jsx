import '../index.css';
import { Link, useNavigate, useLocation } from "react-router-dom";
import authService from "../services/authService";

function Navigation() {
  const navigate = useNavigate();
  const location = useLocation();
  const username = localStorage.getItem("username");

  const handleLogout = (e) => {
    e.preventDefault();
    authService.logout();
    navigate("/");
  };

  const navItems = [
    { path: "/home", icon: "fa-home", label: "Trang chủ" },
    { path: "/employee-list", icon: "fa-users", label: "Nhân viên" },
    { path: "/criterion-group-list", icon: "fa-layer-group", label: "Nhóm tiêu chí" },
    { path: "/criterion-list", icon: "fa-clipboard-list", label: "Tiêu chí" },
    { path: "/project-list", icon: "fa-solid fa-diagram-project", label: "Dự án" },
    { path: "/evaluation-cycle-list", icon: "fa-chart-bar", label: "Chu kỳ đánh giá" },
  ];

  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <div className="sidebar-nav">
      {/* Logo */}

      <div className="brand-logo">Viettel</div>
      
      {/* Navigation Items */}
      {navItems.map((item) => (
        <Link
          key={item.path}
          to={item.path}
          className={`nav-icon ${isActive(item.path) ? 'active' : ''}`}
          title={item.label}
        >
          <i className={`fas ${item.icon}`}></i>
        </Link>
      ))}

      {/* User Info */}
      {username && (
        <div className="user-info">
          <div className="user-avatar" title={username}>
            {username.charAt(0).toUpperCase()}
          </div>
          
          <button 
            className="nav-icon" 
            title="Đổi mật khẩu" 
            onClick={() => navigate("/change-password")}
            onKeyDown={(e) => e.key === 'Enter' && navigate("/change-password")}
          >
            <i className="fas fa-lock"></i>
          </button>
          
          <button 
            className="nav-icon" 
            title="Đăng xuất" 
            onClick={handleLogout}
            onKeyDown={(e) => e.key === 'Enter' && handleLogout(e)}
          >
            <i className="fas fa-sign-out-alt"></i>
          </button>
          
      
        </div>
      )}
    </div>
  );
}

export default Navigation;

