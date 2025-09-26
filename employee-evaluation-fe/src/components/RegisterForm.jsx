import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";

const RegisterForm = () => {
  const [name, setName] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [position, setPosition] = useState("");
  const [department, setDepartment] = useState("");
  const [role, setRole] = useState("EMPLOYEE");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      await authService.register({ name, username, email, password, position, department, role });
      navigate("/login"); 
    } catch (err) {
      setError("Register failed");
    }
  };

  return (
    <form onSubmit={handleRegister}>
      <h2>REGISTER</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Name" required />
      <input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Username" required />
      <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" required />
      <input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" required />
      <input value={position} onChange={(e) => setPosition(e.target.value)} placeholder="Position" required />
      <input value={department} onChange={(e) => setDepartment(e.target.value)} placeholder="Department" required />
      <select value={role} onChange={(e) => setRole(e.target.value)}>
        <option value="EMPLOYEE">Employee</option>
        <option value="ADMIN">Admin</option>
        <option value="MANAGER">Manager</option>
      </select>
      <button type="submit">Register</button>
    </form>
  );
};

export default RegisterForm;
