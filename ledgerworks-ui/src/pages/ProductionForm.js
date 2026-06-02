import { useEffect, useState } from "react";
import API from "../api/api";

function ProductionForm() {

    // =====================================================
    // HEADER
    // =====================================================

    const [productionDate, setProductionDate] =
        useState(
            new Date()
                .toISOString()
                .split("T")[0]
        );

    const [remarks, setRemarks] =
        useState("");

    // =====================================================
    // ITEMS
    // =====================================================

    const [items, setItems] =
        useState([]);

    // =====================================================
    // OUTPUTS
    // =====================================================

    const [outputs, setOutputs] =
        useState([
            {
                itemId: "",
                quantity: ""
            }
        ]);

    // =====================================================
    // AUTO RM
    // =====================================================

    const [rawMaterials, setRawMaterials] =
        useState([]);

    // =====================================================
    // HISTORY
    // =====================================================

    const [history, setHistory] =
        useState([]);

    // =====================================================
    // LOADING
    // =====================================================

    const [loading, setLoading] =
        useState(false);

    // =====================================================
    // LOAD INITIAL DATA
    // =====================================================

    useEffect(() => {

        loadItems();

        loadHistory();

    }, []);

    // =====================================================
    // LOAD ITEMS
    // =====================================================

    const loadItems = async () => {

        try {

            const res =
                await API.get("/items");

            setItems(
                Array.isArray(res.data)
                    ? res.data
                    : []
            );

        } catch (err) {

            console.error(err);
        }
    };

    // =====================================================
    // LOAD HISTORY
    // =====================================================

    const loadHistory = async () => {

        try {

            const res =
                await API.get("/production");

            setHistory(
                Array.isArray(res.data)
                    ? res.data
                    : []
            );

        } catch (err) {

            console.error(err);
        }
    };

    // =====================================================
    // HANDLE OUTPUT CHANGE
    // =====================================================

    const handleOutputChange =
        async (index, field, value) => {

            const updated =
                [...outputs];

            updated[index][field] =
                value;

            setOutputs(updated);

            // =============================================
            // AUTO BOM FETCH
            // =============================================

            const row =
                updated[index];

            if (
                row.itemId &&
                row.quantity
            ) {

                try {

                    const res =
                        await API.get(
                            `/bom/by-fg/${row.itemId}`
                        );

                    const bom =
                        res.data;

                    const rmList =
                        bom.items.map((b) => {

                            const qty =
                                Number(
                                    b.quantity
                                ) *
                                Number(
                                    row.quantity
                                );

                            return {

                                itemName:
                                    b.item.itemName,

                                currentStock:
                                    b.item.currentStock,

                                requiredQty:
                                    qty,

                                unit:
                                    b.item.unit,

                                insufficient:
                                    Number(
                                        b.item.currentStock
                                    ) < qty
                            };
                        });

                    setRawMaterials(rmList);

                } catch (err) {

                    console.error(err);

                    setRawMaterials([]);

                    alert(
                        "BOM not found"
                    );
                }

            } else {

                setRawMaterials([]);
            }
        };

    // =====================================================
    // ADD OUTPUT ROW
    // =====================================================

    const addOutputRow = () => {

        setOutputs([

            ...outputs,

            {
                itemId: "",
                quantity: ""
            }

        ]);
    };

    // =====================================================
    // REMOVE OUTPUT ROW
    // =====================================================

    const removeOutputRow =
        (index) => {

            const updated =
                outputs.filter(
                    (_, i) => i !== index
                );

            setOutputs(updated);
        };

    // =====================================================
    // VALIDATION
    // =====================================================

    const hasInsufficientStock =
        rawMaterials.some(
            r => r.insufficient
        );

    // =====================================================
    // SAVE
    // =====================================================

    const save = async () => {

        if (hasInsufficientStock) {

            alert(
                "Insufficient raw material stock"
            );

            return;
        }

        try {

            setLoading(true);

            const payload = {

                productionDate,

                remarks,

                outputs:

                    outputs
                        .filter(
                            o =>
                                o.itemId &&
                                o.quantity
                        )
                        .map((o) => ({

                            item: {
                                id:
                                    Number(o.itemId)
                            },

                            quantity:
                                Number(o.quantity)
                        }))
            };

            console.log(payload);

            const res =
                await API.post(
                    "/production",
                    payload
                );

            alert(
                `Production saved successfully : ${res.data.productionNumber}`
            );

            // =============================================
            // RESET
            // =============================================

            setProductionDate(
                new Date()
                    .toISOString()
                    .split("T")[0]
            );

            setRemarks("");

            setOutputs([
                {
                    itemId: "",
                    quantity: ""
                }
            ]);

            setRawMaterials([]);

            loadHistory();

        } catch (err) {

            console.error(err);

            alert(
                "Failed to save production"
            );

        } finally {

            setLoading(false);
        }
    };

    // =====================================================
    // UI
    // =====================================================

    return (

        <div style={{ padding: "20px" }}>

            <h2>
                Production Entry
            </h2>

            {/* ========================================= */}
            {/* HEADER */}
            {/* ========================================= */}

            <div
                style={{
                    marginBottom: "20px"
                }}
            >

                <input
                    type="date"
                    value={productionDate}
                    onChange={(e) =>
                        setProductionDate(
                            e.target.value
                        )
                    }
                />

                <input
                    placeholder="Remarks"
                    value={remarks}
                    onChange={(e) =>
                        setRemarks(
                            e.target.value
                        )
                    }
                    style={{
                        marginLeft: "10px",
                        width: "300px"
                    }}
                />

            </div>

            {/* ========================================= */}
            {/* FINISHED GOODS */}
            {/* ========================================= */}

            <h3>
                Finished Goods
            </h3>

            {outputs.map((o, i) => (

                <div
                    key={i}
                    style={{
                        marginBottom: "10px"
                    }}
                >

                    <select
                        value={o.itemId}
                        onChange={(e) =>
                            handleOutputChange(
                                i,
                                "itemId",
                                e.target.value
                            )
                        }
                    >

                        <option value="">
                            Select Item
                        </option>

                        {items.map((item) => (

                            <option
                                key={item.id}
                                value={item.id}
                            >
                                {item.itemName}
                            </option>

                        ))}

                    </select>

                    <input
                        type="number"
                        placeholder="Quantity"
                        value={o.quantity}
                        onChange={(e) =>
                            handleOutputChange(
                                i,
                                "quantity",
                                e.target.value
                            )
                        }
                        style={{
                            marginLeft: "10px"
                        }}
                    />

                    <span
                        style={{
                            marginLeft: "10px",
                            fontWeight: "bold"
                        }}
                    >
                        Stock:
                        {
                            items.find(
                                item =>
                                    item.id === Number(o.itemId)
                            )?.currentStock || 0
                        }
                    </span>

                    <button
                        type="button"
                        onClick={() =>
                            removeOutputRow(i)
                        }
                        style={{
                            marginLeft: "10px"
                        }}
                    >
                        Remove
                    </button>

                </div>

            ))}

            <button onClick={addOutputRow}>
                Add Finished Item
            </button>

            <hr />

            {/* ========================================= */}
            {/* AUTO RM */}
            {/* ========================================= */}

            <h3>
                Auto Raw Material Consumption
            </h3>

            <table
                border="1"
                cellPadding="10"
                width="100%"
            >

                <thead>

                    <tr>

                        <th>
                            Item
                        </th>

                        <th>
                            Required Qty
                        </th>

                        <th>
                            Current Stock
                        </th>

                        <th>
                            Status
                        </th>

                    </tr>

                </thead>

                <tbody>

                    {rawMaterials.length > 0 ? (

                        rawMaterials.map((r, i) => (

                            <tr key={i}>

                                <td>
                                    {r.itemName}
                                </td>

                                <td>
                                    {r.requiredQty}
                                    {" "}
                                    {r.unit}
                                </td>

                                <td>
                                    {r.currentStock}
                                </td>

                                <td>

                                    <span
                                        style={{
                                            color:
                                                r.insufficient
                                                    ? "red"
                                                    : "green",
                                            fontWeight: "bold"
                                        }}
                                    >
                                        {r.insufficient
                                            ? "Insufficient"
                                            : "OK"}
                                    </span>

                                </td>

                            </tr>

                        ))

                    ) : (

                        <tr>

                            <td colSpan="4">
                                No raw materials
                            </td>

                        </tr>
                    )}

                </tbody>

            </table>

            <hr />

            {/* ========================================= */}
            {/* SAVE */}
            {/* ========================================= */}

            <button
                onClick={save}
                disabled={hasInsufficientStock || loading}
                style={{
                    padding: "10px 20px",
                    fontWeight: "bold",
                    cursor: "pointer"
                }}
            >
                {loading
                    ? "Saving..."
                    : "Save Production"}
            </button>

            <hr />

            {/* ========================================= */}
            {/* HISTORY */}
            {/* ========================================= */}

            <h3>
                Production History
            </h3>

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

                    </tr>

                </thead>

                <tbody>

                    {history.length > 0 ? (

                        history.map((h) => (

                            <tr key={h.id}>

                                <td>
                                    {h.productionNumber}
                                </td>

                                <td>
                                    {h.productionDate}
                                </td>

                                <td>
                                    {h.outputs?.[0]?.item?.itemName}
                                </td>

                                <td>
                                    {h.outputs?.[0]?.quantity}
                                </td>

                                <td>
                                    {h.remarks}
                                </td>

                            </tr>

                        ))

                    ) : (

                        <tr>

                            <td colSpan="5">
                                No production history found
                            </td>

                        </tr>
                    )}

                </tbody>

            </table>

        </div>
    );
}

export default ProductionForm;