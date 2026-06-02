import React, { useEffect, useState } from "react";
import axios from "axios";

function MaterialIssuePage() {

    const [items, setItems] = useState([]);

    const [issue, setIssue] = useState({

        issueDate: "",
        remarks: "",

        items: [
            {
                itemId: "",
                quantity: "",
                rate: ""
            }
        ]
    });

    // =====================================================
    // LOAD ITEMS
    // =====================================================

    useEffect(() => {

        axios
            .get("http://localhost:8080/api/items")
            .then(res => setItems(res.data));

    }, []);

    // =====================================================
    // HANDLE CHANGE
    // =====================================================

    const handleItemChange = (
        index,
        field,
        value
    ) => {

        const updated =
            [...issue.items];

        updated[index][field] = value;

        setIssue({
            ...issue,
            items: updated
        });
    };

    // =====================================================
    // ADD ROW
    // =====================================================

    const addRow = () => {

        setIssue({

            ...issue,

            items: [
                ...issue.items,
                {
                    itemId: "",
                    quantity: "",
                    rate: ""
                }
            ]
        });
    };

    // =====================================================
    // SAVE
    // =====================================================

    const saveIssue = async () => {

        const payload = {

            issueDate: issue.issueDate,
            remarks: issue.remarks,

            items: issue.items.map(i => ({

                item: {
                    id: i.itemId
                },

                quantity: i.quantity,
                rate: i.rate
            }))
        };

        await axios.post(
            "http://localhost:8080/api/material-issue",
            payload
        );

        alert("Material Issue Saved");
    };

    return (

        <div>

            <h2>Material Issue</h2>

            <input
                type="date"
                onChange={(e) =>
                    setIssue({
                        ...issue,
                        issueDate: e.target.value
                    })
                }
            />

            <input
                type="text"
                placeholder="Remarks"
                onChange={(e) =>
                    setIssue({
                        ...issue,
                        remarks: e.target.value
                    })
                }
            />

            <hr />

            {
                issue.items.map((row, index) => (

                    <div key={index}>

                        <select
                            onChange={(e) =>
                                handleItemChange(
                                    index,
                                    "itemId",
                                    e.target.value
                                )
                            }
                        >

                            <option>
                                Select Item
                            </option>

                            {
                                items.map(item => (

                                    <option
                                        key={item.id}
                                        value={item.id}
                                    >
                                        {item.itemName}
                                    </option>
                                ))
                            }

                        </select>

                        <input
                            type="number"
                            placeholder="Qty"
                            onChange={(e) =>
                                handleItemChange(
                                    index,
                                    "quantity",
                                    e.target.value
                                )
                            }
                        />

                        <input
                            type="number"
                            placeholder="Rate"
                            onChange={(e) =>
                                handleItemChange(
                                    index,
                                    "rate",
                                    e.target.value
                                )
                            }
                        />

                    </div>
                ))
            }

            <br />

            <button onClick={addRow}>
                Add Item
            </button>

            <button onClick={saveIssue}>
                Save
            </button>

        </div>
    );
}

export default MaterialIssuePage;