import { useEffect, useState } from "react";
import API from "../api/api";

function ProductionList() {

    const [data, setData] =
        useState([]);

    const [search, setSearch] =
        useState("");

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

    const filtered =
        data.filter((d) =>

            d.productionNumber
                ?.toLowerCase()
                .includes(
                    search.toLowerCase()
                )
        );

    return (

        <div style={{ padding: "20px" }}>

            <h2>
                Production List
            </h2>

            <input
                placeholder="Search Production No"
                value={search}
                onChange={(e) =>
                    setSearch(
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

                        <th>
                            Production No
                        </th>

                        <th>
                            Date
                        </th>

                        <th>
                            FG Item
                        </th>

                        <th>
                            Qty
                        </th>

                        <th>
                            Remarks
                        </th>

                        <th>
                            Actions
                        </th>

                    </tr>

                </thead>

                <tbody>

                    {filtered.length > 0 ? (

                        filtered.map((p) => (

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

        </div>
    );
}

export default ProductionList;