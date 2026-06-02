package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.service.BackupService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/backup")
@CrossOrigin(
        origins = "http://localhost:3000",
        allowCredentials = "true"
)
public class BackupController {

    @Autowired
    private BackupService backupService;

    @GetMapping("/create")
    public String createBackup() {

        return backupService.createBackup();
    }
    
    @GetMapping("/list")
    public List<String> getBackups() {

        return backupService.getBackupFiles();
    }
    
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadBackup(
            @PathVariable String fileName) {

        return backupService.downloadBackup(fileName);
    }
    
    @DeleteMapping("/delete/{fileName}")
    public String deleteBackup(
            @PathVariable String fileName) {

        return backupService.deleteBackup(
                fileName
        );
    }
    
    @PostMapping("/restore/{fileName}")
    public String restoreBackup(
            @PathVariable String fileName) {

        return backupService.restoreBackup(
                fileName
        );
    }
}