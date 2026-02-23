package com.revpay.controller;

import com.revpay.dto.request.AddPaymentMethodRequest;
import com.revpay.dto.response.PaymentMethodResponse;
import com.revpay.service.PaymentMethodService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {

    private static final Logger logger = LogManager.getLogger(PaymentMethodController.class);

    @Autowired
    private PaymentMethodService paymentMethodService;

    @PostMapping
    public ResponseEntity<PaymentMethodResponse> addPaymentMethod(
            @Valid @RequestBody AddPaymentMethodRequest request) {
        logger.info("Add payment method request received");
        return ResponseEntity.ok(paymentMethodService.addPaymentMethod(request));
    }

    @GetMapping
    public ResponseEntity<List<PaymentMethodResponse>> getAllPaymentMethods() {
        logger.info("Get all payment methods request received");
        return ResponseEntity.ok(paymentMethodService.getAllPaymentMethods());
    }

    @PutMapping("/{id}/set-default")
    public ResponseEntity<PaymentMethodResponse> setDefault(@PathVariable Long id) {
        logger.info("Set default payment method request received for id: {}", id);
        return ResponseEntity.ok(paymentMethodService.setDefaultPaymentMethod(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePaymentMethod(@PathVariable Long id) {
        logger.info("Delete payment method request received for id: {}", id);
        return ResponseEntity.ok(paymentMethodService.deletePaymentMethod(id));
    }
}