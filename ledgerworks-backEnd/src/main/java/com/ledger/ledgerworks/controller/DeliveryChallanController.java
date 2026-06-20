package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.DeliveryChallan;
import com.ledger.ledgerworks.service.DeliveryChallanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery-challan")
public class DeliveryChallanController {

    @Autowired
    private DeliveryChallanService service;

    // ================= CREATE =================

    @PostMapping
    public DeliveryChallan create(
            @RequestBody DeliveryChallan challan
    ) {

        return service.create(challan);
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public DeliveryChallan update(
            @PathVariable Long id,
            @RequestBody DeliveryChallan challan
    ) {

        return service.update(id, challan);
    }

    // ================= GET ALL =================

    @GetMapping
    public List<DeliveryChallan> getAll() {

        return service.getAll();
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public DeliveryChallan getById(
            @PathVariable Long id
    ) {

        return service.getById(id);
    }

    // ================= CANCEL =================

    @PostMapping("/cancel/{id}")
    public DeliveryChallan cancel(
            @PathVariable Long id
    ) {

        return service.cancel(id);
    }

    // ================= DELIVER =================

    @PostMapping("/deliver/{id}")
    public DeliveryChallan deliver(
            @PathVariable Long id
    ) {

        return service.deliver(id);
    }

    // ================= PRINT PDF =================

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> print(
            @PathVariable Long id
    ) throws Exception {

        DeliveryChallan challan =
                service.getById(id);

        byte[] pdf =
                service.generateChallanPdf(id);

        String fileName =
                challan.getChallanNumber()
                        .replace("/", "-")
                        + ".pdf";

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                fileName +
                                "\""
                )

                .contentType(
                        MediaType.APPLICATION_PDF
                )

                .body(pdf);
    }
}