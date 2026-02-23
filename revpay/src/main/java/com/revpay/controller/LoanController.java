package com.revpay.controller;

import com.revpay.dto.request.LoanApplicationRequest;
import com.revpay.dto.request.LoanRepaymentRequest;
import com.revpay.dto.response.LoanApplicationResponse;
import com.revpay.dto.response.LoanRepaymentResponse;
import com.revpay.service.LoanService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private static final Logger logger = LogManager.getLogger(LoanController.class);

    @Autowired
    private LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<LoanApplicationResponse> applyForLoan(
            @Valid @RequestBody LoanApplicationRequest request) {
        logger.info("Loan application request received");
        return ResponseEntity.ok(loanService.applyForLoan(request));
    }

    @GetMapping
    public ResponseEntity<List<LoanApplicationResponse>> getAllLoans() {
        logger.info("Get all loans request received");
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanApplicationResponse>> getLoansByStatus(
            @PathVariable String status) {
        logger.info("Get loans by status request received: {}", status);
        return ResponseEntity.ok(loanService.getLoansByStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanApplicationResponse> getLoanById(@PathVariable Long id) {
        logger.info("Get loan by id request received: {}", id);
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @PostMapping("/{id}/repay")
    public ResponseEntity<LoanRepaymentResponse> makeRepayment(
            @PathVariable Long id,
            @Valid @RequestBody LoanRepaymentRequest request) {
        logger.info("Loan repayment request received for loan: {}", id);
        return ResponseEntity.ok(loanService.makeRepayment(id, request));
    }
}