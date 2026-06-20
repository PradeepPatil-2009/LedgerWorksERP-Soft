import { useEffect, useState } from "react";
import API from "../api/api";
import { useTableControls } from "../components/useTableControls";
import Pagination from "../components/Pagination";
import SortableTh from "../components/SortableTh";

function ProductionList() {

    const [data, setData] =
        useState([]);

    const tc = useTableControls(data, {
        searchKeys: ["productionNumber", "productionDate", "remarks"],
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

    useEffect(() => {

        loadData();

    }, []);

    const loadData = async () => {

        try {

            const res =
                await API.get("/production");

            setData(
                Array.isArray(res.data)
                    ? res.data
                    : []
            );

        } catch (err) {

            console.error(err);
        }
    };

    const remove = async (id) => {

        const confirmDelete =
            window.confirm(
                "Delete production?"
            );

        if (!confirmDelete) {

            return;
        }

        try {

            await API.delete(
                `/production/${id}`
            );

            alert(
                "Production deleted"
            );

            loadData();

        } catch (err) {

            console.error(err);

            alert(
                "Delete failed"
            );
        }
    };

    return (

        <div style={{ padding: "20px" }}>

            <h2>
                Production List
            </h2>

            <input
                placeholder="Search..."
                value={query}
                onChange={(e) =>
                    setQuery(
                        e.target.value
                    )
                }
                style={{
                    marginBottom: "20px",
                    width: "300px",
                    padding: "8px"
                }}
            />

            <table
                border="1"
                cellPadding="10"
                width="100%"
            >

                <thead>

                    <tr>

                        <SortableTh field="productionNumber" controls={tc}>
                            Production No
                        </SortableTh>

                        <SortableTh field="productionDate" controls={tc}>
                            Date
                        </SortableTh>

                        <th>
                            FG Item
                        </th>

                        <th>
                            Qty
                        </th>

                        <SortableTh field="remarks" controls={tc}>
                            Remarks
                        </SortableTh>

                        <th>
                            Actions
                        </th>

                    </tr>

                </thead>

                <tbody>

                    {pageItems.length > 0 ? (

                        pageItems.map((p) => (

                            <tr key={p.id}>

                                <td>
                                    {p.productionNumber}
                                </td>

                                <td>
                                    {p.productionDate}
                                </td>

                                <td>
                                    {
                                        p.outputs?.[0]
                                            ?.item
                                            ?.itemName
                                    }
                                </td>

                                <td>
                                    {
                                        p.outputs?.[0]
                                            ?.quantity
                                    }
                                </td>

                                <td>
                                    {p.remarks}
                                </td>

                                <td>

                                    <button
                                        onClick={() =>
                                            remove(p.id)
                                        }
                                    >
                                        Delete
                                    </button>

                                </td>

                            </tr>

                        ))

                    ) : (

                        <tr>

                            <td colSpan="6">
                                No production found
                            </td>

                        </tr>
                    )}

                </tbody>

            </table>

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

export default ProductionList;