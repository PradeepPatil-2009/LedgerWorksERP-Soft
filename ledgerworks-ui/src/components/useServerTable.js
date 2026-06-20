import { useCallback, useEffect, useRef, useState } from "react";

// =====================================================
// useServerTable
//
// SERVER-side SEARCH + PAGINATION for list tables backed by a Spring Data
// Page endpoint. Unlike useTableControls (which slices a bounded in-memory
// array), this hook owns the page/query STATE and asks the server for one
// page at a time. Use it only for tables whose rows are unbounded.
//
// fetcher contract:
//   fetcher({ page, size, q }) -> Promise resolving to a Spring Page, i.e.
//     { content: [...], totalElements, totalPages, number, size }
//   page is 0-based (Spring's convention). The caller is responsible for
//   issuing the request (usually via the shared axios API instance) and
//   returning res.data.
//
// Usage:
//   const { rows, page, setPage, totalPages, total, query, setQuery,
//           loading, reload } = useServerTable(
//     ({ page, size, q }) =>
//       API.get("/audit-logs", { params: { page, size, q } })
//         .then((res) => res.data),
//     { pageSize: 20 }
//   );
//
//   // 0-based server page maps to the 1-based <Pagination> display:
//   <Pagination
//     page={page + 1}
//     totalPages={totalPages}
//     total={total}
//     onPrev={() => setPage(page - 1)}
//     onNext={() => setPage(page + 1)}
//   />
//
// Behaviour:
//   - page is 0-based; setQuery resets page back to 0.
//   - Refetches on mount and whenever page or query change.
//   - Race-safe: each request carries a sequence id; only the newest
//     response is applied, so out-of-order resolutions are ignored.
//   - Error-safe: a failed/ignored request keeps the previously shown rows
//     (no flash to empty); loading is always cleared for the live request.
//   - Tolerant of a plain array payload (no .content) — falls back to
//     treating it as the row list with sensible totals.
// =====================================================

function readPage(payload, size) {
  // Spring Page shape: { content, totalElements, totalPages, number }.
  if (payload && Array.isArray(payload.content)) {
    const content = payload.content;
    const total = Number.isFinite(payload.totalElements)
      ? payload.totalElements
      : content.length;
    const totalPages = Number.isFinite(payload.totalPages)
      ? payload.totalPages
      : Math.max(1, Math.ceil(total / (size || 1)));
    return { rows: content, total, totalPages };
  }

  // Defensive fallback: a bare array (or anything else) — treat as one page.
  const rows = Array.isArray(payload) ? payload : [];
  return {
    rows,
    total: rows.length,
    totalPages: Math.max(1, Math.ceil(rows.length / (size || 1))),
  };
}

export function useServerTable(fetcher, options = {}) {
  const { pageSize = 20 } = options || {};
  const size =
    Number.isFinite(pageSize) && pageSize > 0 ? Math.floor(pageSize) : 20;

  const [page, setPageState] = useState(0);
  const [query, setQueryState] = useState("");
  const [rows, setRows] = useState([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);

  // Monotonic request id: only the latest issued request may apply its
  // result. Survives re-renders without triggering one.
  const requestId = useRef(0);
  // Keep the freshest fetcher without making it a load() dependency.
  const fetcherRef = useRef(fetcher);
  fetcherRef.current = fetcher;

  const load = useCallback(
    async (nextPage, nextQuery) => {
      const id = requestId.current + 1;
      requestId.current = id;
      setLoading(true);
      try {
        const payload = await fetcherRef.current({
          page: nextPage,
          size,
          q: nextQuery,
        });
        // Drop stale responses (a newer request was issued meanwhile).
        if (id !== requestId.current) return;
        const parsed = readPage(payload, size);
        setRows(parsed.rows);
        setTotal(parsed.total);
        setTotalPages(parsed.totalPages);
      } catch (e) {
        // Keep prior rows on error; just stop the spinner for the live call.
        if (id !== requestId.current) return;
      } finally {
        if (id === requestId.current) {
          setLoading(false);
        }
      }
    },
    [size]
  );

  // Refetch on mount and whenever page or query change.
  useEffect(() => {
    load(page, query);
  }, [load, page, query]);

  // setPage clamps to a non-negative integer; out-of-range values are ignored.
  const setPage = useCallback((next) => {
    setPageState((prev) => {
      const resolved = typeof next === "function" ? next(prev) : next;
      const numeric = Number(resolved);
      if (!Number.isFinite(numeric)) return prev;
      return Math.max(0, Math.floor(numeric));
    });
  }, []);

  // setQuery resets back to the first page.
  const setQuery = useCallback((next) => {
    setQueryState((prev) =>
      typeof next === "function" ? next(prev) : next
    );
    setPageState(0);
  }, []);

  // reload re-runs the current page/query without changing state.
  const reload = useCallback(() => {
    load(page, query);
  }, [load, page, query]);

  return {
    rows,
    page,
    setPage,
    totalPages,
    total,
    query,
    setQuery,
    loading,
    reload,
  };
}

export default useServerTable;
