import { useMemo, useState } from "react";

// =====================================================
// useTableControls
//
// Shared client-side SEARCH + SORT + PAGINATION for list tables.
// Drop-in for any page that already holds rows in a state array.
//
// Usage:
//   const controls = useTableControls(customers, {
//     searchKeys: ["name", "gstNumber", "state", "phone"],
//     pageSize: 10,
//   });
//   const {
//     query, setQuery,
//     page, setPage,
//     totalPages, pageItems, total,
//     sortKey, sortDir, toggleSort,   // <-- sorting
//   } = controls;
//
//   // search box:
//   <input value={query}
//          onChange={(e) => setQuery(e.target.value)}
//          placeholder="Search..." />
//
//   // sortable headers (see SortableTh.js) — pass the whole controls:
//   <SortableTh field="name" controls={controls}>Name</SortableTh>
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
//   - Sort is applied to the FILTERED list BEFORE pagination, so
//     pageItems reflects filter + sort + page.
//   - toggleSort(key) cycles: a new key starts at 'asc'; the active
//     key cycles 'asc' -> 'desc' -> cleared (sortKey null, sortDir null).
//   - Comparator: null/undefined sort LAST in both directions; numeric
//     values (or numeric strings) compare numerically, everything else
//     by case-insensitive localeCompare. The sort is stable.
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

// Treat actual numbers and numeric strings ("42", " 3.5 ") as numbers.
function asNumber(value) {
  if (typeof value === "number") {
    return Number.isFinite(value) ? value : null;
  }
  if (typeof value === "string") {
    const trimmed = value.trim();
    if (trimmed === "") return null;
    const num = Number(trimmed);
    return Number.isFinite(num) ? num : null;
  }
  return null;
}

// Comparator for two cell values.
//   - null/undefined always sort LAST (regardless of direction).
//   - both numeric (number or numeric string) -> numeric compare.
//   - otherwise -> case-insensitive localeCompare on string form.
function compareValues(a, b) {
  const aEmpty = a == null;
  const bEmpty = b == null;
  if (aEmpty && bEmpty) return 0;
  if (aEmpty) return 1; // a is "bigger" so it lands last
  if (bEmpty) return -1;

  const an = asNumber(a);
  const bn = asNumber(b);
  if (an != null && bn != null) {
    if (an < bn) return -1;
    if (an > bn) return 1;
    return 0;
  }

  return toText(a).localeCompare(toText(b), undefined, {
    sensitivity: "base",
  });
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
  const [sortKey, setSortKey] = useState(null);
  const [sortDir, setSortDir] = useState(null); // 'asc' | 'desc' | null

  const safeItems = Array.isArray(items) ? items : [];

  // toggleSort cycles: new key -> asc; same key -> asc -> desc -> cleared.
  const toggleSort = (key) => {
    if (key == null) return;
    if (key !== sortKey) {
      setSortKey(key);
      setSortDir("asc");
      return;
    }
    if (sortDir === "asc") {
      setSortDir("desc");
    } else if (sortDir === "desc") {
      setSortKey(null);
      setSortDir(null);
    } else {
      // sortDir was null but key matched (defensive) -> restart at asc.
      setSortDir("asc");
    }
  };

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

  // Sort the FILTERED list before pagination. Stable: we sort an array
  // of [row, index] pairs and fall back to the original index on ties.
  const sorted = useMemo(() => {
    if (!sortKey || (sortDir !== "asc" && sortDir !== "desc")) {
      return filtered;
    }
    const factor = sortDir === "desc" ? -1 : 1;
    const indexed = filtered.map((row, index) => [row, index]);
    indexed.sort((a, b) => {
      const av = a[0] == null ? undefined : a[0][sortKey];
      const bv = b[0] == null ? undefined : b[0][sortKey];
      const cmp = compareValues(av, bv);
      // Empties always last, so don't flip them with direction.
      if (av == null || bv == null) {
        if (cmp !== 0) return cmp;
        return a[1] - b[1];
      }
      if (cmp !== 0) return cmp * factor;
      return a[1] - b[1];
    });
    return indexed.map((pair) => pair[0]);
  }, [filtered, sortKey, sortDir]);

  const total = sorted.length;
  const totalPages = Math.max(1, Math.ceil(total / size));

  // Clamp the page into a valid range whenever the data shrinks.
  const safePage = Math.min(Math.max(1, page), totalPages);

  const pageItems = useMemo(() => {
    const start = (safePage - 1) * size;
    return sorted.slice(start, start + size);
  }, [sorted, safePage, size]);

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
    sortKey,
    sortDir,
    toggleSort,
  };
}

export default useTableControls;
