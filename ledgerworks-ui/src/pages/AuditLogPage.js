import { useEffect, useState } from "react";

import API from "../api/api";

import { useToast } from "../components/Toast";

function AuditLogPage() {

  const toast = useToast();

  const [logs, setLogs] = useState([]);

  const [loading, setLoading] = useState(false);

  // ================= LOAD =================

  const loadLogs = async () => {

    setLoading(true);

    try {

      const res = await API.get("/audit-logs");

      setLogs(Array.isArray(res.data) ? res.data : []);

    } catch (err) {

      console.error(err);

      toast.error("Failed to load audit logs");

    } finally {

      setLoading(false);
    }
  };

  useEffect(() => {
    loadLogs();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // ================= FORMAT =================

  const formatTimestamp = (ts) => {

    if (!ts) {
      return "";
    }

    try {
      return new Date(ts).toLocaleString();
    } catch (e) {
      return ts;
    }
  };

  return (

    <div style={{ padding: "20px" }}>

      <h2>Audit Log</h2>

      <div style={{ marginBottom: "10px" }}>

        <button onClick={loadLogs}>
          Refresh
        </button>

      </div>

      {loading ? (

        <p>Loading...</p>

      ) : (

        <div style={{ overflowX: "auto" }}>

          <table
            border="1"
            cellPadding="8"
            width="100%"
          >

            <thead>

              <tr>
                <th>Time</th>
                <th>User</th>
                <th>Action</th>
                <th>Entity</th>
                <th>Detail</th>
              </tr>

            </thead>

            <tbody>

              {logs.length > 0 ? (

                logs.map((log) => (

                  <tr key={log.id}>

                    <td>{formatTimestamp(log.timestamp)}</td>

                    <td>{log.username}</td>

                    <td>{log.action}</td>

                    <td>{log.entityType}</td>

                    <td>{log.detail}</td>

                  </tr>
                ))

              ) : (

                <tr>
                  <td colSpan="5">
                    No Audit Events Found
                  </td>
                </tr>

              )}

            </tbody>

          </table>

        </div>
      )}

    </div>
  );
}

export default AuditLogPage;
