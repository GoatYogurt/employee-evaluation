import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";

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
    <form className="change-password-form" onSubmit={handleSubmit}>
      <h2>Change Password</h2>

      {error && <p style={{ color: "red" }}>{error}</p>}
      {success && <p style={{ color: "green" }}>{success}</p>}

      <input
        type="password"
        placeholder="Old Password"
        value={oldPassword}
        onChange={(e) => setOldPassword(e.target.value)}
        required
      />
      <input
        type="password"
        placeholder="New Password"
        value={newPassword}
        onChange={(e) => setNewPassword(e.target.value)}
        required
      />

      <button type="submit">Change Password</button>

      {/* Back to Home */}
      <span
        onClick={() => navigate("/home")}
        style={{ cursor: "pointer", color: "blue", marginLeft: "10px" }}
      >
        Back to Home
      </span>
    </form>
  );
};

export default ChangePasswordForm;
