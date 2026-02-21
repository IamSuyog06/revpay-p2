package com.revpay.controller;

import com.revpay.dto.request.TransactionFilterRequest;
import com.revpay.dto.response.TransactionResponse;
import com.revpay.service.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger logger = LogManager.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        logger.info("Get all transactions request received");
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        logger.info("Get transaction by id request received: {}", id);
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping("/filter")
    public ResponseEntity<List<TransactionResponse>> filterTransactions(
            @RequestBody TransactionFilterRequest request) {
        logger.info("Filter transactions request received");
        return ResponseEntity.ok(transactionService.filterTransactions(request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TransactionResponse>> searchTransactions(
            @RequestParam String keyword) {
        logger.info("Search transactions request received with keyword: {}", keyword);
        return ResponseEntity.ok(transactionService.searchTransactions(keyword));
    }

    @GetMapping("/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        logger.info("Export transactions to CSV request received");
        transactionService.exportToCsv(response);
    }
}