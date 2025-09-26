import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";

const LoginForm = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await authService.login(username, password);
      navigate("/home"); 
    } catch (err) {
      setError("Invalid username or password");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>LOGIN</h2>
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
      placeholder="Password" 
      style={{color: "black"}}
      value={password} 
      onChange={(e) => setPassword(e.target.value)} 
      required />

      <button type="submit">Login</button>

      <p>
        Don't have an account? <button type="button" onClick={() => navigate("/register")}>Register</button>
      </p>
    </form>
  );
};

export default LoginForm;
