import "../index.css";
import { Link, useNavigate, useLocation } from "react-router-dom";
import authService from "../services/authService";
import React, { useState, useEffect, useRef } from "react";

function Navigation() {
  const navigate = useNavigate();
  const location = useLocation();
  const [userInfo, setUserInfo] = useState({});
  const [showPopup, setShowPopup] = useState(false);
  const popupRef = useRef(null);

  // ✅ Load thông tin user từ localStorage và cập nhật khi thay đổi
  useEffect(() => {
    const loadUserInfo = () => {
      const userData = {
        staffCode: localStorage.getItem("staffCode"),
        fullName: localStorage.getItem("fullName"),
        email: localStorage.getItem("email"),
        department: localStorage.getItem("department"),
        role: localStorage.getItem("role"),
        level: localStorage.getItem("level"),
        username: localStorage.getItem("username"),
      };
      setUserInfo(userData);
    };

    loadUserInfo();
    window.addEventListener("storage", loadUserInfo);
    return () => window.removeEventListener("storage", loadUserInfo);
  }, []);

  useEffect(() => {
    function handleClickOutside(event) {
      if (popupRef.current && !popupRef.current.contains(event.target)) {
        setShowPopup(false);
      }
    }
    if (showPopup) {
      document.addEventListener("mousedown", handleClickOutside);
    }
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [showPopup]);

  // ✅ Logout
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
    { path: "/project-list", icon: "fa-diagram-project", label: "Dự án" },
    { path: "/evaluation-cycle-list", icon: "fa-chart-bar", label: "Chu kỳ đánh giá" },
  ];

  const isActive = (path) => location.pathname === path;

  return (
    <div className="sidebar-nav">
      <div className="brand-logo">Viettel</div>

      {navItems.map((item) => (
        <Link
          key={item.path}
          to={item.path}
          className={`nav-icon ${isActive(item.path) ? "active" : ""}`}
          title={item.label}
        >
          <i className={`fas ${item.icon}`}></i>
        </Link>
      ))}

      {/* Avatar luôn hiển thị */}
      <div
        className="user-info"
        onClick={() => setShowPopup(!showPopup)}
        style={{ cursor: "pointer", marginTop: "auto", textAlign: "center" }}
      >
        <div className="user-avatar" title={userInfo.username || "User"}>
          {userInfo.username ? userInfo.username.charAt(0).toUpperCase() : "?"}
        </div>
      </div>

      {showPopup && (
        <div className="user-popup" ref={popupRef}>
          <div className="user-popup-title">Thông tin nhân viên</div>
          <div className="user-popup-row">
            <span className="user-popup-label">Mã NV: </span> {userInfo.staffCode || ""}
          </div>
          <div className="user-popup-row">
            <span className="user-popup-label">Họ và tên: </span> {userInfo.fullName || ""}
          </div>
          <div className="user-popup-row">
            <span className="user-popup-label">Email: </span> {userInfo.email || ""}
          </div>
          <div className="user-popup-row">
            <span className="user-popup-label">Phòng/Ban: </span> {userInfo.department || ""}
          </div>
          <div className="user-popup-row">
            <span className="user-popup-label">Chức vụ: </span> {userInfo.role || ""}
          </div>
          <div className="user-popup-row">
            <span className="user-popup-label">Cấp bậc: </span> {userInfo.level || ""}
          </div>

          <div className="user-popup-actions">

            <button
              className="user-popup-btn"
              onClick={() => {
                setShowPopup(false);
                navigate("/change-password");
              }}
            >
              Đổi mật khẩu
            </button>

            <button className="user-popup-btn-logout" onClick={handleLogout}>
              Đăng xuất
            </button>

          </div>
        </div>
      )}
    </div>
  );
}

export default Navigation;
