import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";
import "../index.css";

const RegisterForm = () => {
  const [fullName, setFullName] = useState("");
  const [staffCode, setStaffCode] = useState("");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [department, setDepartment] = useState("");
  const [role, setRole] = useState("EMPLOYEE");
  const [level, setLevel] = useState("FRESHER");
  const [error, setError] = useState("");
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      await authService.register({
        fullName,
        staffCode,
        username,
        email,
        password,
        department,
        role,
        level,
      });
      setShowSuccessPopup(true);
    } catch (err) {
      toast.error("Đăng ký thất bại. Vui lòng thử lại.");
      setError("Đăng ký thất bại. Vui lòng thử lại.");

    }
  };

  const handleBackToLogin = () => {
    setShowSuccessPopup(false);
    navigate("/");
  };

  return (
    <>
      <div className="register-container">
        <form className="register-form" onSubmit={handleRegister}>
          <h2>ĐĂNG KÝ</h2>
          {error && <p className="error">{error}</p>}

          <input
            type="text"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            placeholder="Nhập họ và tên"
            required
          />
          <input
            type="text"
            value={staffCode}
            onChange={(e) => setStaffCode(e.target.value)}
            placeholder="Nhập mã nhân viên"
            required
          />
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Tên đăng nhập"
            required
          />
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Email"
            required
          />
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Mật khẩu"
            required
          />
          <input
            type="text"
            value={department}
            onChange={(e) => setDepartment(e.target.value)}
            placeholder="Phòng ban"
            required
          />

          <select value={role} onChange={(e) => setRole(e.target.value)}>
            <option value="PGDTT">PGDTT</option>
            <option value="ADMIN">ADMIN</option>
            <option value="PM">PM</option>
            <option value="DEV">DEV</option>
            <option value="TESTER">TESTER</option>
            <option value="BA">BA</option>
            <option value="UIUX">UIUX</option>
            <option value="AI">AI</option>
            <option value="DATA">DATA</option>
            <option value="QA">QA</option>
            <option value="VHKT">VHKT</option>
            <option value="MKT">MKT</option>
          </select>

          <select value={level} onChange={(e) => setLevel(e.target.value)}>
            <option value="FRESHER">FRESHER</option>
            <option value="JUNIOR">JUNIOR</option>
            <option value="JUNIOR_PLUS">JUNIOR_PLUS</option>
            <option value="MIDDLE">MIDDLE</option>
            <option value="MIDDLE_PLUS">MIDDLE_PLUS</option>
            <option value="SENIOR">SENIOR</option>
          </select>

          <button type="submit">Đăng ký</button>
        </form>
      </div>

      {showSuccessPopup && (
        <div className="success-popup">
          <div className="success-popup-content">
            <h3>Đăng ký thành công!</h3>
            <p>
              Chúc mừng bạn đã đăng ký tài khoản thành công. Bạn có thể đăng
              nhập ngay bây giờ.
            </p>
            <button onClick={handleBackToLogin}>
              Quay lại trang đăng nhập
            </button>
          </div>
        </div>
      )}
    </>
  );
};

export default RegisterForm;
