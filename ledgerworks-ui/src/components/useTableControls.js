import { useMemo, useState } from "react";

// =====================================================
// useTableControls
//
// Shared client-side SEARCH + PAGINATION for list tables.
// Drop-in for any page that already holds rows in a state array.
//
// Usage:
//   const {
//     query, setQuery,
//     page, setPage,
//     totalPages, pageItems, total,
//   } = useTableControls(customers, {
//     searchKeys: ["name", "gstNumber", "state", "phone"],
//     pageSize: 10,
//   });
//
//   // search box:
//   <input value={query}
//          onChange={(e) => setQuery(e.target.value)}
//          placeholder="Search..." />
//
//   // render the page slice instead of the full array:
//   pageItems.map((row) => ...)
//
//   // pager (see Pagination.js):
//   <Pagination page={page} totalPages={totalPages} total={total}
//               onPrev={() => setPage(page - 1)}
//               onNext={() => setPage(page + 1)} />
//
// Behaviour:
//   - Filter is a case-insensitive substring match.
//       * If searchKeys is non-empty, only those keys are matched.
//       * If searchKeys is empty, every string-ish value of the row
//         is matched.
//   - setQuery resets page back to 1.
//   - page is clamped to [1, totalPages]; totalPages is at least 1.
//   - pageItems is the slice for the current page; total is the
//     filtered count (not the raw count).
//   - Null-safe: non-array items behaves like an empty list, null/
//     undefined rows and missing keys are skipped without throwing.
// =====================================================

function toText(value) {
  if (value == null) return "";
  if (typeof value === "string") return value;
  if (typeof value === "number" || typeof value === "boolean") {
    return String(value);
  }
  return "";
}

function rowMatches(row, needle, searchKeys) {
  if (row == null) return false;

  // Per-key search.
  if (Array.isArray(searchKeys) && searchKeys.length > 0) {
    for (let i = 0; i < searchKeys.length; i += 1) {
      const text = toText(row[searchKeys[i]]).toLowerCase();
      if (text.indexOf(needle) !== -1) return true;
    }
    return false;
  }

  // Fallback: scan all string-ish values of the row.
  if (typeof row === "object") {
    const values = Object.values(row);
    for (let i = 0; i < values.length; i += 1) {
      const text = toText(values[i]).toLowerCase();
      if (text && text.indexOf(needle) !== -1) return true;
    }
    return false;
  }

  // Primitive row.
  return toText(row).toLowerCase().indexOf(needle) !== -1;
}

export function useTableControls(items, options = {}) {
  const { searchKeys = [], pageSize = 10 } = options || {};

  const [query, setQueryState] = useState("");
  const [page, setPageState] = useState(1);

  const safeItems = Array.isArray(items) ? items : [];

  // setQuery resets pagination back to the first page.
  const setQuery = (next) => {
    setQueryState(typeof next === "function" ? next : next);
    setPageState(1);
  };

  const size =
    Number.isFinite(pageSize) && pageSize > 0 ? Math.floor(pageSize) : 10;

  const filtered = useMemo(() => {
    const needle = (query || "").trim().toLowerCase();
    if (!needle) return safeItems;
    return safeItems.filter((row) => rowMatches(row, needle, searchKeys));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [safeItems, query, size, JSON.stringify(searchKeys)]);

  const total = filtered.length;
  const totalPages = Math.max(1, Math.ceil(total / size));

  // Clamp the page into a valid range whenever the data shrinks.
  const safePage = Math.min(Math.max(1, page), totalPages);

  const pageItems = useMemo(() => {
    const start = (safePage - 1) * size;
    return filtered.slice(start, start + size);
  }, [filtered, safePage, size]);

  // setPage is clamped on the way in too, so callers can pass page +/- 1.
  const setPage = (next) => {
    setPageState((prev) => {
      const resolved = typeof next === "function" ? next(prev) : next;
      const numeric = Number(resolved);
      if (!Number.isFinite(numeric)) return prev;
      return Math.min(Math.max(1, Math.floor(numeric)), totalPages);
    });
  };

  return {
    query,
    setQuery,
    page: safePage,
    setPage,
    totalPages,
    pageItems,
    total,
  };
}

export default useTableControls;
