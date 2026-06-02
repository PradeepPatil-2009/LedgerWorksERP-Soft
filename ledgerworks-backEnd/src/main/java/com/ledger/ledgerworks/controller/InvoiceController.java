package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.DeliveryChallan;
import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.repository.DeliveryChallanRepository;
import com.ledger.ledgerworks.repository.InvoiceRepository;
import com.ledger.ledgerworks.service.InvoiceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:3000")
public class InvoiceController {

	@Autowired
	private InvoiceService service;

	@Autowired
	private DeliveryChallanRepository deliveryChallanRepository;

	@Autowired
	private InvoiceRepository invoiceRepository;

	// GET ALL INVOICES

	@GetMapping
	public List<Invoice> getAll() {

		return service.getAll();
	}

	// CREATE MANUAL INVOICE

	@PostMapping
	public Invoice create(@RequestBody Invoice invoice) {

		return service.create(invoice);
	}

	// CONVERT CHALLAN TO INVOICE

	@PostMapping("/convert/{id}")
	public Invoice convert(@PathVariable Long id) {

		return service.convertFromChallan(id);
	}

	// GET INVOICE BY CHALLAN ID

	@GetMapping("/by-challan/{challanId}")
	public ResponseEntity<?> getByChallan(

			@PathVariable Long challanId) {

		DeliveryChallan dc = deliveryChallanRepository.findById(challanId)
				.orElseThrow(() -> new RuntimeException("Challan not found"));

		if (dc.getInvoice() == null) {

			return ResponseEntity.ok(null);
		}

		return ResponseEntity.ok(dc.getInvoice());
	}

	// SEARCH INVOICE BY NUMBER

	@GetMapping("/search")
	public ResponseEntity<?> searchInvoice(

			@RequestParam String invoiceNumber) {

		Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber.trim())
				.orElseThrow(() -> new RuntimeException("Invoice not found"));

		return ResponseEntity.ok(invoice);
	}

	// GENERATE PDF

	@GetMapping("/{id}/pdf")
	public ResponseEntity<byte[]> pdf(

			@PathVariable Long id,

			@RequestParam(defaultValue = "ORIGINAL") String copyType

	) throws Exception {

		Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));

		byte[] pdf = service.generateInvoicePdf(id, copyType);

		String fileName = invoice.getInvoiceNumber().replace("/", "-") + ".pdf";

		return ResponseEntity.ok()

				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")

				.contentType(MediaType.APPLICATION_PDF)

				.body(pdf);
	}
}