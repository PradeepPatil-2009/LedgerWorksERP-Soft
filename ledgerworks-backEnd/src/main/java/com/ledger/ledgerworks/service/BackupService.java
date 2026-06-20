package com.ledger.ledgerworks.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

	private static final Logger log =
			LoggerFactory.getLogger(BackupService.class);

	@Value("${backup.dir:${user.home}/LedgerWorksBackup}")
	private String backupDir;

	@Value("${spring.datasource.url:}")
	private String datasourceUrl;

	@Value("${spring.datasource.username:root}")
	private String dbUser;

	@Value("${spring.datasource.password:}")
	private String dbPassword;

	// Allow only simple "*.sql" names; reject path separators and ".."
	// so callers cannot traverse outside the backup directory.
	private static final java.util.regex.Pattern SAFE_BACKUP_NAME =
			java.util.regex.Pattern.compile("^[A-Za-z0-9_.-]+\\.sql$");

	private boolean isValidBackupName(String fileName) {

		if (fileName == null
				|| fileName.contains("/")
				|| fileName.contains("\\")
				|| fileName.contains("..")) {

			return false;
		}

		return SAFE_BACKUP_NAME.matcher(fileName).matches();
	}

	private String resolveDatabaseName() {

		// Extract the schema name from a JDBC URL such as
		// jdbc:mysql://host:3306/<dbname>?params
		if (datasourceUrl == null || datasourceUrl.isBlank()) {
			return "ledgerworks";
		}

		String url = datasourceUrl;

		int slash = url.lastIndexOf('/');

		if (slash < 0 || slash == url.length() - 1) {
			return "ledgerworks";
		}

		String afterSlash = url.substring(slash + 1);

		int query = afterSlash.indexOf('?');

		String dbName =
				query >= 0
						? afterSlash.substring(0, query)
						: afterSlash;

		return dbName.isBlank() ? "ledgerworks" : dbName;
	}

	public String createBackup() {

	    try {

	        String backupFolder = backupDir;

	        new File(backupFolder).mkdirs();

	        String fileName =
	                "ledgerworks_"
	                        + new SimpleDateFormat(
	                        "yyyyMMdd_HHmmss")
	                        .format(new Date())
	                        + ".sql";

	        String backupFile =
	                backupFolder + "/" + fileName;

	        List<String> command = new ArrayList<>();

	        // Resolved from PATH so it works on macOS/Linux/Windows.
	        command.add("mysqldump");

	        command.add("-u");
	        command.add(dbUser);

	        // MySQL accepts the password only when it is non-blank;
	        // passing an empty -p would prompt interactively and hang.
	        if (dbPassword != null && !dbPassword.isBlank()) {
	            command.add("-p" + dbPassword);
	        }

	        command.add(resolveDatabaseName());

	        ProcessBuilder pb =
	                new ProcessBuilder(command);

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

	        log.warn("Backup failed with exit code {}", result);

	        return "Backup Failed";

	    } catch(Exception ex) {

	        log.error("Backup error", ex);

	        return "Backup Error : "
	                + ex.getMessage();
	    }
	}



	private void cleanupOldBackups() {

	    File folder =
	            new File(backupDir);

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

	        if (!files[i].delete()) {
	            log.warn("Could not delete old backup {}",
	                    files[i].getAbsolutePath());
	        }
	    }
	}

    public List<String> getBackupFiles() {

        File folder = new File(backupDir);

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

            if (!isValidBackupName(fileName)) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            Path path =
                    Paths.get(backupDir, fileName);

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

            log.error("Download backup error", ex);

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    public String deleteBackup(String fileName) {

        if (!isValidBackupName(fileName)) {

            return "Delete Failed";
        }

        File file =
                new File(backupDir, fileName);

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

            if (!isValidBackupName(fileName)) {

                return "Restore Failed";
            }

            String backupFile =
                    backupDir + "/" + fileName;

            List<String> command = new ArrayList<>();

            // Resolved from PATH so it works on macOS/Linux/Windows.
            command.add("mysql");

            command.add("-u");
            command.add(dbUser);

            if (dbPassword != null && !dbPassword.isBlank()) {
                command.add("-p" + dbPassword);
            }

            command.add(resolveDatabaseName());

            ProcessBuilder pb =
                    new ProcessBuilder(command);

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

            log.warn("Restore failed with exit code {}", result);

            return "Restore Failed";

        } catch (Exception ex) {

            log.error("Restore error", ex);

            return "Restore Error : "
                    + ex.getMessage();
        }
    }
}
