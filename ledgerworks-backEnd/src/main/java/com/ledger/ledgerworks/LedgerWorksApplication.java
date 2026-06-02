package com.ledger.ledgerworks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LedgerWorksApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                LedgerWorksApplication.class,
                args
        );
    }
}