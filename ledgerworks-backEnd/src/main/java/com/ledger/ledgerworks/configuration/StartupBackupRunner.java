package com.ledger.ledgerworks.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ledger.ledgerworks.service.BackupService;

@Component
public class StartupBackupRunner
        implements CommandLineRunner {

    private final BackupService backupService;

    public StartupBackupRunner(
            BackupService backupService) {

        this.backupService = backupService;
    }

    @Override
    public void run(String... args)
            throws Exception {

        System.out.println(
                backupService.createBackup()
        );
    }
}