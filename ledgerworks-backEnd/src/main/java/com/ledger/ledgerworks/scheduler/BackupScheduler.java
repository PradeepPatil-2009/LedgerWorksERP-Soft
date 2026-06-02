package com.ledger.ledgerworks.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ledger.ledgerworks.service.BackupService;

@Component
public class BackupScheduler {

    private final BackupService backupService;

    public BackupScheduler(
            BackupService backupService) {

        this.backupService = backupService;
    }

    @Scheduled(
            cron = "0 0 22 * * ?"
    )
    public void autoBackup() {

        String result =
                backupService.createBackup();

        System.out.println(result);
    }
}