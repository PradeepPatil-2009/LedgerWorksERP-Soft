// =====================================================
// SortableTh
//
// A clickable table header that drives column sorting through the
// controls object returned by useTableControls.
//
// PROP CONTRACT (this is the exact interface page agents must use):
//   <SortableTh
//     field="name"        // the row key to sort by (string, required)
//     controls={controls} // the WHOLE object from useTableControls
//     style={{ width: 120 }} // optional extra <th> styles (merged in)
//   >
//     Name                // header label (children)
//   </SortableTh>
//
// Behaviour:
//   - Clicking the header calls controls.toggleSort(field), which
//     cycles asc -> desc -> cleared (see useTableControls).
//   - Shows a sort indicator after the label:
//       * ' ▲' when this column is the active asc sort,
//       * ' ▼' when this column is the active desc sort,
//       * a faint ' ⇅' otherwise (column is sortable but not active).
//   - The <th> is cursor:pointer and non-selectable so double-clicks
//     don't highlight the label text.
//
// Example wiring on a list page:
//   const controls = useTableControls(rows, { searchKeys: ["name"] });
//   ...
//   <thead>
//     <tr>
//       <SortableTh field="name" controls={controls}>Name</SortableTh>
//       <SortableTh field="amount" controls={controls}>Amount</SortableTh>
//     </tr>
//   </thead>
// =====================================================

function SortableTh({ field, controls, children, style }) {
  const sortKey = controls ? controls.sortKey : null;
  const sortDir = controls ? controls.sortDir : null;
  const active = sortKey === field;

  let indicator;
  if (active && sortDir === "asc") {
    indicator = <span style={{ color: "#1e293b" }}> ▲</span>;
  } else if (active && sortDir === "desc") {
    indicator = <span style={{ color: "#1e293b" }}> ▼</span>;
  } else {
    indicator = <span style={{ color: "#cbd5e1" }}> ⇅</span>;
  }

  const handleClick = () => {
    if (controls && typeof controls.toggleSort === "function") {
      controls.toggleSort(field);
    }
  };

  return (
    <th
      onClick={handleClick}
      style={{
        cursor: "pointer",
        userSelect: "none",
        ...style,
      }}
    >
      {children}
      {indicator}
    </th>
  );
}

export default SortableTh;
