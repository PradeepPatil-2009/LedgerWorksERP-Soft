package com.ledger.ledgerworks.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;



import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class BackupService {

	public String createBackup() {

	    try {

	        String backupFolder =
	                "D:/LedgerWorksBackup";

	        new File(backupFolder).mkdirs();

	        String fileName =
	                "ledgerworks_"
	                        + new SimpleDateFormat(
	                        "yyyyMMdd_HHmmss")
	                        .format(new Date())
	                        + ".sql";

	        String backupFile =
	                backupFolder + "/" + fileName;

	        ProcessBuilder pb =
	                new ProcessBuilder(

	                        "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump",

	                        "-u",
	                        "root",

	                        "-pLedger@2026",

	                        "ledgerworks"
	                );

	        pb.redirectOutput(
	                new File(backupFile)
	        );

	        Process process =
	                pb.start();

	        int result =
	                process.waitFor();

	        if(result == 0) {

	            cleanupOldBackups();

	            return "Backup Created : "
	                    + backupFile;
	        }

	        return "Backup Failed";

	    } catch(Exception ex) {

	        ex.printStackTrace();

	        return "Backup Error : "
	                + ex.getMessage();
	    }
	}
    
    
    
	private void cleanupOldBackups() {

	    File folder =
	            new File(
	                    "D:/LedgerWorksBackup"
	            );

	    File[] files =
	            folder.listFiles(
	                    (dir, name) ->
	                            name.endsWith(".sql")
	            );

	    if(files == null) {
	        return;
	    }

	    Arrays.sort(
	            files,
	            (a, b) -> Long.compare(
	                    b.lastModified(),
	                    a.lastModified()
	            )
	    );

	    for(int i = 30;
	        i < files.length;
	        i++) {

	        files[i].delete();
	    }
	}
    
    public List<String> getBackupFiles() {

        File folder = new File("D:/LedgerWorksBackup");

        File[] files = folder.listFiles();

        List<String> backups = new ArrayList<>();

        if (files != null) {

            for (File file : files) {

                if (file.getName().endsWith(".sql")) {

                    backups.add(file.getName());
                }
            }
        }

        backups.sort(Collections.reverseOrder());

        return backups;
    }
    public ResponseEntity<Resource> downloadBackup(
            String fileName) {

        try {

            Path path =
                    Paths.get(
                            "D:/LedgerWorksBackup",fileName
                    
                            
                    );

            Resource resource =
                    new UrlResource(
                            path.toUri()
                    );

            if (!resource.exists()) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            return ResponseEntity.ok()
                    .contentType(
                            MediaType.APPLICATION_OCTET_STREAM
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\""
                                    + resource.getFilename()
                                    + "\""
                    )
                    .body(resource);

        } catch (Exception ex) {

            ex.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }
    
    public String deleteBackup(String fileName) {

        File file =
                new File(
                        "D:/LedgerWorksBackup/"
                                + fileName
                );

        if (file.exists()) {

            if (file.delete()) {

                return "Backup Deleted";
            }
        }

        return "Delete Failed";
    }
    public String restoreBackup(
            String fileName) {

        try {

            String backupFile =
                    "D:/LedgerWorksBackup/"
                            + fileName;

            ProcessBuilder pb =
                    new ProcessBuilder(

                            "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql",

                            "-u",
                            "root",

                            "-pLedger@2026",

                            "ledgerworks"
                    );

            pb.redirectInput(
                    new File(backupFile)
            );

            Process process =
                    pb.start();

            int result =
                    process.waitFor();

            if (result == 0) {

                return "Database Restored Successfully";
            }

            return "Restore Failed";

        } catch (Exception ex) {

            ex.printStackTrace();

            return "Restore Error : "
                    + ex.getMessage();
        }
    }
}
