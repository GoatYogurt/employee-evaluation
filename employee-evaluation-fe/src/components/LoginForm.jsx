import React, { use, useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";
import '../index.css';
import { useContext } from "react";
import { ToastContext } from "../contexts/ToastProvider";


const LoginForm = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { toast } = useContext(ToastContext);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try { 
      await authService.login(username, password);
      toast.success("Đăng nhập thành công!");
      navigate("/employee-list"); 
      // Sau khi login thành công
      localStorage.setItem("token", data.token);
      localStorage.setItem("userInfo", JSON.stringify(data.data)); // lưu thông tin nhân viên
    

    } catch (err) {
      toast.error(err || "Sai tên đăng nhập hoặc mật khẩu!"); 
      setError("Invalid username or password");
    }
  };

  return (
     <form className="login-form" onSubmit={handleSubmit}>
        <h2>Đăng Nhập</h2>
        {error && <p style={{ color: "red" }}>{error}</p>}
        <input 
        type="text" 
        placeholder="Username" 
        style={{color: "black"}}
        value={username} 
        onChange={(e) => setUsername(e.target.value)} 
        required />

        <input 
        type="password" 
        placeholder="Mật khẩu" 
        style={{color: "black"}}
        value={password} 
        onChange={(e) => setPassword(e.target.value)} 
        onKeyDown={(e) => e.key === "Enter" && handleSubmit(e)}
        required />

        <button type="submit">Đăng Nhập</button>

        <p className="dont-have">
          Chưa có mật khẩu? <span type="button" onClick={() => navigate("/register")}>Đăng ký</span>
        </p>
    </form>
  );
};

export default LoginForm;
