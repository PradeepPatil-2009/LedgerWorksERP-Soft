import React, {
    useState,
    useEffect
} from "react";

import API from "../api/api";

import { useToast } from "../components/Toast";

function BackupManagementPage() {

    const toast = useToast();

    const [message, setMessage] =
        useState("");

    const [backups, setBackups] =
        useState([]);

    useEffect(() => {

        loadBackups();

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const loadBackups = async () => {

        try {

            const response =
                await API.get(
                    "/backup/list"
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
                await API.get(
                    "/backup/create"
                );

            setMessage(
                response.data
            );

            toast.success("Backup Created");

            loadBackups();

        } catch (error) {

            setMessage(
                "Backup Failed"
            );

            toast.error("Backup Failed");
        }
    };

    const downloadBackup = async (fileName) => {

        try {

            const response =
                await API.get(
                    "/backup/download/" +
                        encodeURIComponent(fileName),
                    {
                        responseType: "blob"
                    }
                );

            // CREATE BACKUP FILE

            const file =
                new Blob(
                    [response.data],
                    {
                        type: "application/octet-stream"
                    }
                );

            // CREATE DOWNLOAD LINK

            const fileURL =
                window.URL.createObjectURL(file);

            const link =
                document.createElement("a");

            link.href = fileURL;

            link.setAttribute(
                "download",
                fileName
            );

            document.body.appendChild(link);

            // AUTO DOWNLOAD

            link.click();

            // CLEANUP

            link.remove();

            window.URL.revokeObjectURL(fileURL);

        } catch (error) {

            console.error(error);

            toast.error("Download Failed");
        }
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
                await API.delete(
                    "/backup/delete/" +
                        encodeURIComponent(fileName)
                );

            setMessage(
                response.data
            );

            toast.success("Backup Deleted");

            loadBackups();

        } catch (error) {

            setMessage(
                "Delete Failed"
            );

            toast.error("Delete Failed");
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
            await API.post(
                "/backup/restore/" +
                    encodeURIComponent(fileName)
            );

        setMessage(
            response.data
        );

        toast.success("Backup Restored");

    } catch (error) {

        setMessage(
            "Restore Failed"
        );

        toast.error("Restore Failed");
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

            <div className="table-scroll">

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

        </div>
    );
}

export default BackupManagementPage;