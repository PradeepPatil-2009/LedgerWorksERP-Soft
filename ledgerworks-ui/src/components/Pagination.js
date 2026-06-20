// =====================================================
// Pagination
//
// Tiny pager that pairs with useTableControls.
//
// PROP CONTRACT (this is the exact interface page agents must use):
//   <Pagination
//     page={page}              // current page, 1-based (number)
//     totalPages={totalPages}  // total number of pages, >= 1 (number)
//     total={total}            // filtered record count (number)
//     onPrev={() => setPage(page - 1)}  // called when "Prev" clicked
//     onNext={() => setPage(page + 1)}  // called when "Next" clicked
//   />
//
//   - The component itself does NO clamping; it only disables the
//     Prev/Next buttons at the bounds. useTableControls.setPage already
//     clamps, so onPrev/onNext can safely pass page - 1 / page + 1.
//   - When total <= 0 (no records), the component renders nothing.
//   - Renders the text: "Showing page X of Y (N records)".
//
// Example wiring on a list page:
//   const { page, setPage, totalPages, total, pageItems } =
//     useTableControls(rows, { searchKeys: ["name"], pageSize: 10 });
//   ...
//   <Pagination
//     page={page}
//     totalPages={totalPages}
//     total={total}
//     onPrev={() => setPage(page - 1)}
//     onNext={() => setPage(page + 1)}
//   />
// =====================================================

function Pagination({ page, totalPages, total, onPrev, onNext }) {
  const records = Number(total) || 0;

  // Hide entirely when there is nothing to page through.
  if (records <= 0) return null;

  const currentPage = Number(page) || 1;
  const pages = Math.max(1, Number(totalPages) || 1);

  const atStart = currentPage <= 1;
  const atEnd = currentPage >= pages;

  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        gap: "10px",
        marginTop: "12px",
        flexWrap: "wrap",
      }}
    >
      <button
        type="button"
        onClick={onPrev}
        disabled={atStart}
      >
        Prev
      </button>

      <span style={{ fontSize: "14px", color: "#475569" }}>
        Showing page {currentPage} of {pages} ({records} records)
      </span>

      <button
        type="button"
        onClick={onNext}
        disabled={atEnd}
      >
        Next
      </button>
    </div>
  );
}

export default Pagination;
