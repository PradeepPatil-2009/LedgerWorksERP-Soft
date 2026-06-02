package com.ledger.ledgerworks.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ledger.ledgerworks.service.BackupService;

@Component
public class StartupBackupRunner
        implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(StartupBackupRunner.class);

    private final BackupService backupService;

    public StartupBackupRunner(
            BackupService backupService) {

        this.backupService = backupService;
    }

    @Override
    public void run(String... args)
            throws Exception {

        log.info(backupService.createBackup());
    }
}