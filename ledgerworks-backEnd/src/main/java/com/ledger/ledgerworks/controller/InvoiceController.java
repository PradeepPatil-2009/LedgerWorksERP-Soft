package com.ledger.ledgerworks.controller;

import com.ledger.ledgerworks.entity.DeliveryChallan;
import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.repository.DeliveryChallanRepository;
import com.ledger.ledgerworks.repository.InvoiceRepository;
import com.ledger.ledgerworks.service.InvoiceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
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

	// GET ALL INVOICES (server-paginated)
	// Optional sort (e.g. "id,desc"; default id DESC so newly-created rows
	// appear first) and free-text q across the obvious text columns.

	@GetMapping("/page")
	public Page<Invoice> getPage(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(required = false) String sort,
			@RequestParam(required = false) String q) {

		Pageable pageable = PageRequest.of(page, size, parseSort(sort));

		if (q == null || q.isBlank()) {

			return invoiceRepository.findAll(pageable);
		}

		return invoiceRepository.searchPage(q.trim(), pageable);
	}

	// Parse a "field,dir" sort param into a Sort. Defaults to id DESC so the
	// newest rows surface first.

	private Sort parseSort(String sort) {

		if (sort == null || sort.isBlank()) {

			return Sort.by(Sort.Order.desc("id"));
		}

		String[] parts = sort.split(",");

		String field = parts[0].trim();

		if (field.isEmpty()) {

			field = "id";
		}

		Sort.Direction direction =
				(parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()))
						? Sort.Direction.ASC
						: Sort.Direction.DESC;

		return Sort.by(direction, field);
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