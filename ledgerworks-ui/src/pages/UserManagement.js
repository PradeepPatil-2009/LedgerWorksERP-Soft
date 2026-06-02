import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useToast } from "../components/Toast";

function UserManagement() {
  const navigate = useNavigate();
  const toast = useToast();

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const [newUser, setNewUser] = useState({
    username: "",
    password: "",
    role: "USER",
  });

  const token = localStorage.getItem("token");

  // ✅ FETCH USERS
  const fetchUsers = useCallback(async () => {
    try {
      const res = await fetch("http://localhost:8080/api/users", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!res.ok) throw new Error("API failed");

      const data = await res.json();
      setUsers(data);
    } catch (err) {
      console.error("Fetch Users Error:", err);
    } finally {
      setLoading(false);
    }
  }, [token]);

  // ✅ AUTH FIX (IMPORTANT)
  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    fetchUsers();
  }, [token, fetchUsers, navigate]);

  // ✅ CREATE USER
  const createUser = async () => {
    if (!newUser.username || !newUser.password) {
      toast.error("Enter username & password");
      return;
    }

    try {
      await fetch("http://localhost:8080/api/users", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(newUser),
      });

      toast.success("User Created");
      setNewUser({ username: "", password: "", role: "USER" });
      fetchUsers();
    } catch (err) {
      console.error("Create Error:", err);
      toast.error("Failed to create user");
    }
  };

  // ✅ DELETE USER
  const deleteUser = async (id) => {
    if (!window.confirm("Delete user?")) return;

    try {
      await fetch(`http://localhost:8080/api/users/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      toast.success("User Deleted");
      fetchUsers();
    } catch (err) {
      console.error("Delete Error:", err);
      toast.error("Failed to delete user");
    }
  };

  // ✅ UPDATE ROLE
  const updateRole = async (id, role) => {
    try {
      await fetch(
        `http://localhost:8080/api/users/${id}/role?role=${role}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
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
          <option>USER</option>
          <option>ADMIN</option>
        </select>

        <button onClick={createUser}>Create</button>
      </div>

      <br />

      {/* TABLE */}
      <div className="table-scroll">
      <table border="1" style={{ margin: "0 auto" }}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Role</th>
            <th>Change</th>
            <th>Delete</th>
          </tr>
        </thead>

        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.id}</td>
              <td>{u.username}</td>
              <td>{u.role}</td>

              <td>
                <select
                  value={u.role}
                  onChange={(e) => updateRole(u.id, e.target.value)}
                >
                  <option>USER</option>
                  <option>ADMIN</option>
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
    </div>
  );
}

export default UserManagement;