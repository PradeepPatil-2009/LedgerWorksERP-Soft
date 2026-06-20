import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useToast } from "../components/Toast";
import { useTableControls } from "../components/useTableControls";
import Pagination from "../components/Pagination";
import SortableTh from "../components/SortableTh";

const ROLES = ["ADMIN", "ACCOUNTANT", "USER", "VIEWER"];

// Client-side mirror of the server password policy. The server stays the
// source of truth (returns 400 on violation); this is just an early guard.
const PASSWORD_HINT = "At least 8 characters, including a letter and a number.";

function isValidPassword(pw) {
  return /^(?=.*[A-Za-z])(?=.*\d).{8,}$/.test(pw || "");
}

function UserManagement() {
  const navigate = useNavigate();
  const toast = useToast();

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const tc = useTableControls(users, {
    searchKeys: ["username", "role"],
    pageSize: 10,
  });

  const {
    query,
    setQuery,
    page,
    setPage,
    totalPages,
    pageItems,
    total,
  } = tc;

  const [newUser, setNewUser] = useState({
    username: "",
    password: "",
    role: "USER",
  });

  // Auth is via the HttpOnly cookie (sent automatically with credentials), so
  // there is no token to read here. ProtectedRoute already gates access; we
  // only handle a mid-session 401 by bouncing back to login.
  const fetchUsers = useCallback(async () => {
    try {
      const res = await fetch("http://localhost:8080/api/users");
      if (res.status === 401) {
        navigate("/login");
        return;
      }
      if (!res.ok) throw new Error("API failed");
      const data = await res.json();
      setUsers(data);
    } catch (err) {
      console.error("Fetch Users Error:", err);
    } finally {
      setLoading(false);
    }
  }, [navigate]);

  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  const createUser = async () => {
    if (!newUser.username || !newUser.password) {
      toast.error("Enter username & password");
      return;
    }

    if (!isValidPassword(newUser.password)) {
      toast.error(
        "Password must be at least 8 characters and include a letter and a number."
      );
      return;
    }

    try {
      const res = await fetch("http://localhost:8080/api/users", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newUser),
      });

      if (!res.ok) {
        // The server is the source of truth — surface its 400 message
        // (e.g. the password-policy text) when present.
        const serverMessage = await res.text().catch(() => "");
        toast.error(serverMessage || "Failed to create user");
        return;
      }

      toast.success("User Created");
      setNewUser({ username: "", password: "", role: "USER" });
      fetchUsers();
    } catch (err) {
      console.error("Create Error:", err);
      toast.error("Failed to create user");
    }
  };

  const deleteUser = async (id) => {
    if (!window.confirm("Delete user?")) return;

    try {
      await fetch(`http://localhost:8080/api/users/${id}`, {
        method: "DELETE",
      });

      toast.success("User Deleted");
      fetchUsers();
    } catch (err) {
      console.error("Delete Error:", err);
      toast.error("Failed to delete user");
    }
  };

  const updateRole = async (id, role) => {
    try {
      await fetch(
        `http://localhost:8080/api/users/${id}/role?role=${role}`,
        { method: "PUT" }
      );

      toast.success("Role Updated");
      fetchUsers();
    } catch (err) {
      console.error("Update Error:", err);
      toast.error("Failed to update role");
    }
  };

  if (loading) return <h3>Loading...</h3>;

  return (
    <div style={{ textAlign: "center", marginTop: "40px" }}>
      <h2>User Management</h2>

      {/* CREATE */}
      <div>
        <input
          placeholder="Username"
          value={newUser.username}
          onChange={(e) =>
            setNewUser({ ...newUser, username: e.target.value })
          }
        />

        <input
          type="password"
          placeholder="Password"
          value={newUser.password}
          onChange={(e) =>
            setNewUser({ ...newUser, password: e.target.value })
          }
        />

        <select
          value={newUser.role}
          onChange={(e) =>
            setNewUser({ ...newUser, role: e.target.value })
          }
        >
          {ROLES.map((r) => (
            <option key={r} value={r}>
              {r}
            </option>
          ))}
        </select>

        <button onClick={createUser}>Create</button>

        <div
          style={{
            fontSize: "12px",
            color: "var(--lw-muted)",
            marginTop: "6px",
          }}
        >
          {PASSWORD_HINT}
        </div>
      </div>

      <br />

      <input
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search..."
        style={{ marginBottom: "10px" }}
      />

      {/* TABLE */}
      <div className="table-scroll">
        <table border="1" style={{ margin: "0 auto" }}>
          <thead>
            <tr>
              <SortableTh field="id" controls={tc}>ID</SortableTh>
              <SortableTh field="username" controls={tc}>Username</SortableTh>
              <SortableTh field="role" controls={tc}>Role</SortableTh>
              <th>Change</th>
              <th>Delete</th>
            </tr>
          </thead>

          <tbody>
            {pageItems.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.username}</td>
                <td>{u.role}</td>

                <td>
                  <select
                    value={u.role}
                    onChange={(e) => updateRole(u.id, e.target.value)}
                  >
                    {ROLES.map((r) => (
                      <option key={r} value={r}>
                        {r}
                      </option>
                    ))}
                  </select>
                </td>

                <td>
                  <button onClick={() => deleteUser(u.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Pagination
        page={page}
        totalPages={totalPages}
        total={total}
        onPrev={() => setPage(page - 1)}
        onNext={() => setPage(page + 1)}
      />
    </div>
  );
}

export default UserManagement;
