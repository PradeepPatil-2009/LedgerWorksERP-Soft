package com.ledger.ledgerworks.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Summary returned after a bulk data import.
 *
 * imported = rows successfully saved
 * skipped  = rows ignored (blank / duplicate / invalid)
 * errors   = human readable messages describing each skipped/failed row
 */
public class ImportResult {

    private int imported;

    private int skipped;

    private List<String> errors = new ArrayList<>();

    // ================= HELPERS =================

    public void addImported() {
        this.imported++;
    }

    public void addSkipped(String reason) {
        this.skipped++;
        if (reason != null && !reason.isBlank()) {
            this.errors.add(reason);
        }
    }

    // ================= GETTERS / SETTERS =================

    public int getImported() {
        return imported;
    }

    public void setImported(int imported) {
        this.imported = imported;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
