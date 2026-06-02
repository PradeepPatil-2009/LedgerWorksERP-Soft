import React, {
    useState,
    useEffect
} from "react";

import axios from "axios";

function BackupManagementPage() {

    const [message, setMessage] =
        useState("");

    const [backups, setBackups] =
        useState([]);

    useEffect(() => {

        loadBackups();

    }, []);

    const loadBackups = async () => {

        try {

            const response =
                await axios.get(
                    "http://localhost:8080/api/backup/list"
                );

            setBackups(
                response.data
            );

        } catch (error) {

            console.error(
                "Backup list error",
                error
            );
        }
    };

    const createBackup = async () => {

        try {

            const response =
                await axios.get(
                    "http://localhost:8080/api/backup/create"
                );

            setMessage(
                response.data
            );

            loadBackups();

        } catch (error) {

            setMessage(
                "Backup Failed"
            );
        }
    };

    const downloadBackup = (fileName) => {

        window.open(
            `http://localhost:8080/api/backup/download/${fileName}`,
            "_blank"
        );
    };

    const deleteBackup = async (
        fileName
    ) => {

        const confirmDelete =
            window.confirm(
                "Delete backup file?"
            );

        if (!confirmDelete) {

            return;
        }

        try {

            const response =
                await axios.delete(
                    `http://localhost:8080/api/backup/delete/${fileName}`
                );

            setMessage(
                response.data
            );

            loadBackups();

        } catch (error) {

            setMessage(
                "Delete Failed"
            );
        }
    };

    const restoreBackup = async (
    fileName
) => {

    const confirmRestore =
        window.confirm(
            "Restore this backup?"
        );

    if (!confirmRestore) {

        return;
    }

    try {

        const response =
            await axios.post(
                `http://localhost:8080/api/backup/restore/${fileName}`
            );

        setMessage(
            response.data
        );

    } catch (error) {

        setMessage(
            "Restore Failed"
        );
    }
};

    return (

        <div className="container">

            <h2>
                Backup Management
            </h2>

            <button
                onClick={createBackup}
            >
                Create Backup
            </button>

            <br />
            <br />

            <b>{message}</b>

            <br />
            <br />

            <h3>
                Backup History
            </h3>

            <table
                border="1"
                cellPadding="10"
                style={{
                    borderCollapse: "collapse",
                    width: "100%"
                }}
            >

                <thead>

                    <tr>

                        <th>
                            Sr No
                        </th>

                        <th>
                            Backup File
                        </th>

                        <th>
                            Download
                        </th>

                        <th>
                            Delete
                        </th>
                        <th>Restore</th>

                    </tr>

                </thead>

                <tbody>

                    {backups.map(
                        (
                            file,
                            index
                        ) => (

                            <tr
                                key={index}
                            >

                                <td>
                                    {index + 1}
                                </td>

                                <td>
                                    {file}
                                </td>

                                <td>

                                    <button
                                        onClick={() =>
                                            downloadBackup(
                                                file
                                            )
                                        }
                                    >
                                        Download
                                    </button>

                                </td>

                                <td>

                                    <button
                                        onClick={() =>
                                            deleteBackup(
                                                file
                                            )
                                        }
                                    >
                                        Delete
                                    </button>

                                </td>
                                <td>

                                    <button
                                        onClick={() =>
                                            restoreBackup(file)
                                        }
                                    >
                                        Restore
                                    </button>

                                </td>

                            </tr>

                        )
                    )}

                </tbody>

            </table>

        </div>
    );
}

export default BackupManagementPage;