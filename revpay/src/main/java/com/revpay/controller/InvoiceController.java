package com.revpay.controller;

import com.revpay.dto.request.CreateInvoiceRequest;
import com.revpay.dto.response.InvoiceResponse;
import com.revpay.service.InvoiceService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private static final Logger logger = LogManager.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request) {
        logger.info("Create invoice request received");
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        logger.info("Get all invoices request received");
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByStatus(
            @PathVariable String status) {
        logger.info("Get invoices by status request received: {}", status);
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        logger.info("Get invoice by id request received: {}", id);
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @PutMapping("/{id}/send")
    public ResponseEntity<InvoiceResponse> sendInvoice(@PathVariable Long id) {
        logger.info("Send invoice request received: {}", id);
        return ResponseEntity.ok(invoiceService.sendInvoice(id));
    }

    @PutMapping("/{id}/mark-paid")
    public ResponseEntity<InvoiceResponse> markAsPaid(@PathVariable Long id) {
        logger.info("Mark invoice as paid request received: {}", id);
        return ResponseEntity.ok(invoiceService.markAsPaid(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<InvoiceResponse> cancelInvoice(@PathVariable Long id) {
        logger.info("Cancel invoice request received: {}", id);
        return ResponseEntity.ok(invoiceService.cancelInvoice(id));
    }
}