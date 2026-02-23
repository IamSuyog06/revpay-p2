package com.revpay.controller;

import com.revpay.dto.request.AddMoneyRequest;
import com.revpay.dto.request.MoneyRequestDto;
import com.revpay.dto.request.SendMoneyRequest;
import com.revpay.dto.request.WithdrawRequest;
import com.revpay.dto.response.MoneyRequestResponse;
import com.revpay.dto.response.TransactionResponse;
import com.revpay.dto.response.WalletResponse;
import com.revpay.service.WalletService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private static final Logger logger = LogManager.getLogger(WalletController.class);

    @Autowired
    private WalletService walletService;

    @GetMapping
    public ResponseEntity<WalletResponse> getWallet() {
        return ResponseEntity.ok(walletService.getWallet());
    }

    @PostMapping("/add-money")
    public ResponseEntity<TransactionResponse> addMoney(@Valid @RequestBody AddMoneyRequest request) {
        return ResponseEntity.ok(walletService.addMoney(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(walletService.withdraw(request));
    }

    @PostMapping("/send-money")
    public ResponseEntity<TransactionResponse> sendMoney(@Valid @RequestBody SendMoneyRequest request) {
        return ResponseEntity.ok(walletService.sendMoney(request));
    }

    @PostMapping("/request-money")
    public ResponseEntity<MoneyRequestResponse> requestMoney(@Valid @RequestBody MoneyRequestDto request) {
        return ResponseEntity.ok(walletService.requestMoney(request));
    }

    @GetMapping("/requests/incoming")
    public ResponseEntity<List<MoneyRequestResponse>> getIncomingRequests() {
        return ResponseEntity.ok(walletService.getIncomingRequests());
    }

    @GetMapping("/requests/outgoing")
    public ResponseEntity<List<MoneyRequestResponse>> getOutgoingRequests() {
        return ResponseEntity.ok(walletService.getOutgoingRequests());
    }

    @PutMapping("/requests/{requestId}/accept")
    public ResponseEntity<TransactionResponse> acceptRequest(
            @PathVariable Long requestId,
            @RequestParam String transactionPin) {
        return ResponseEntity.ok(walletService.acceptMoneyRequest(requestId, transactionPin));
    }

    @PutMapping("/requests/{requestId}/decline")
    public ResponseEntity<String> declineRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(walletService.declineMoneyRequest(requestId));
    }

    @PutMapping("/requests/{requestId}/cancel")
    public ResponseEntity<String> cancelRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(walletService.cancelMoneyRequest(requestId));
    }
}