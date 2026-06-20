package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.dto.GstStateResponse;
import com.ledger.ledgerworks.service.GstUtilityService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gst-utility")
public class GstUtilityController {

    private final GstUtilityService service;

    public GstUtilityController(GstUtilityService service) {
        this.service = service;
    }

    // GET /api/gst-utility/state?gst=27AB...
    @GetMapping("/state")
    public GstStateResponse state(@RequestParam String gst) {

        String code = service.codeFromGst(gst);
        String stateName = service.stateFromGst(gst);

        return new GstStateResponse(code, stateName);
    }
}
