// src/Login.jsx
import { use, useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [token, setToken] = useState(null);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();

        try {
            const res = await fetch("http://localhost:8080/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ username, password }),
            });

            if (!res.ok) {
                throw new Error(`HTTP error! Status: ${res.status}`);
            }

            const data = await res.json();
            setToken(data.accessToken);

            // Store in localStorage for later requests
            localStorage.setItem("accessToken", data.accessToken);
            localStorage.setItem("username", username);
            navigate("/");
            setError(null);
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div style={{ maxWidth: 300, margin: "2rem auto" }}>
            <h2>Login</h2>
            <form onSubmit={handleLogin}>
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button type="submit">Login</button>
            </form>
            {error && <p style={{ color: "red" }}>{error}</p>}
            {token && (
                <p style={{ color: "green" }}>
                    Logged in! Redirecting...
                </p>
            )}
        </div>
    );
}
