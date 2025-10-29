import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";
import "../index.css";


const ChangePasswordForm = () => {
  const navigate = useNavigate();
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    try {
      await authService.changePassword(oldPassword, newPassword);

      // ✅ Không logout nữa
      setSuccess("Password changed successfully!");

      // 👉 Nếu muốn quay về home sau vài giây
      setTimeout(() => navigate("/home"), 1500);
    } catch (err) {
      setError("Old password is incorrect or failed to change password");
    }
  };

  return (
    <div className="change-password-container">
      <form className="change-password-form" onSubmit={handleSubmit}>
      <h2>Đổi mật khẩu</h2>

      {error && <p style={{ color: "red" }}>{error}</p>}
      {success && <p style={{ color: "green" }}>{success}</p>}

      <input
        type="password"
        placeholder="Mật khẩu cũ"
        value={oldPassword}
        onChange={(e) => setOldPassword(e.target.value)}
        required
      />
      <input
        type="password"
        placeholder="Mật khẩu mới"
        value={newPassword}
        onChange={(e) => setNewPassword(e.target.value)}
        required
      />

      <button className="btn-change-pass" type="submit">Xác nhận đổi</button>

      {/* Back to Home */}
      <span className="back-to-home"
        onClick={() => navigate("/home")}> Về trang chủ </span>
    </form>
    </div>
  );
};

export default ChangePasswordForm;
