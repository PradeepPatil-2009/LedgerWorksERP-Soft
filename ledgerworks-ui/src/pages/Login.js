import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../api/api";
import { applyAuthSession } from "../api/authToken";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    setError("");

    const trimmedUsername = username.trim();

    if (!trimmedUsername || !password) {
      setError("Please enter both username and password.");
      return;
    }

    setLoading(true);

    try {
      const res = await loginUser(trimmedUsername, password);

      // Persist the session (token + identity) so every request is authenticated.
      applyAuthSession({
        token: res.token,
        username: res.username,
        role: res.role,
      });

      navigate("/dashboard");
    } catch (err) {
      console.error(err);
      setError(
        err?.response?.data?.message || "Invalid username or password."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <form className="login-card" onSubmit={handleLogin}>
        <h1 className="login-brand">LedgerWorks ERP</h1>

        <p className="login-subtitle">Sign in to your account</p>

        {error && (
          <div className="login-error" role="alert">
            {error}
          </div>
        )}

        <div className="form-group">
          <label htmlFor="login-username">Username</label>
          <input
            id="login-username"
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label htmlFor="login-password">Password</label>
          <input
            id="login-password"
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            disabled={loading}
          />
        </div>

        <button
          type="submit"
          className="btn btn-primary login-submit"
          disabled={loading}
        >
          {loading ? "Signing in..." : "Login"}
        </button>
      </form>
    </div>
  );
}

export default Login;
