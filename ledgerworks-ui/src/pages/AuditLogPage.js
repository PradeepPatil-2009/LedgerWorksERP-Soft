import { useCallback } from "react";

import API from "../api/api";

import { useToast } from "../components/Toast";

import { useServerTable } from "../components/useServerTable";

import Pagination from "../components/Pagination";

// Audit Log is ADMIN-only and unbounded, so it reads one server page at a
// time via the paginated GET /api/audit-logs (Spring Page) instead of the
// client-side useTableControls used by the bounded list pages.

function AuditLogPage() {

  const toast = useToast();

  // ================= FETCHER =================
  // Resolves to a Spring Page ({ content, totalElements, totalPages, number }).
  // Only meaningful params are sent: page 0 and an empty query are omitted so
  // the server falls back to its own defaults (page 0, size 20). size matches
  // the backend default and is left to the server too.
  const fetchLogs = useCallback(
    async ({ page, q }) => {
      const params = {};
      if (page) {
        params.page = page;
      }
      if (q && q.trim() !== "") {
        params.q = q;
      }

      try {
        const res =
          Object.keys(params).length > 0
            ? await API.get("/audit-logs", { params })
            : await API.get("/audit-logs");
        return res.data;
      } catch (err) {
        console.error(err);
        toast.error("Failed to load audit logs");
        throw err;
      }
    },
    [toast]
  );

  const {
    rows,
    page,
    setPage,
    totalPages,
    total,
    query,
    setQuery,
    loading,
    reload,
  } = useServerTable(fetchLogs, { pageSize: 20 });

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

        <button onClick={reload}>
          Refresh
        </button>

        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search..."
          style={{ marginLeft: "8px" }}
        />

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
                <th>Path</th>
                <th>Detail</th>
              </tr>

            </thead>

            <tbody>

              {rows.length > 0 ? (

                rows.map((log) => (

                  <tr key={log.id}>

                    <td>{formatTimestamp(log.timestamp)}</td>

                    <td>{log.actor || log.username}</td>

                    <td>{log.action}</td>

                    <td>{log.entityType}</td>

                    <td>{log.requestUri}</td>

                    <td>{log.detail}</td>

                  </tr>
                ))

              ) : (

                <tr>
                  <td colSpan="6">
                    No Audit Events Found
                  </td>
                </tr>

              )}

            </tbody>

          </table>

          <Pagination
            page={page + 1}
            totalPages={totalPages}
            total={total}
            onPrev={() => setPage(page - 1)}
            onNext={() => setPage(page + 1)}
          />

        </div>
      )}

    </div>
  );
}

export default AuditLogPage;
