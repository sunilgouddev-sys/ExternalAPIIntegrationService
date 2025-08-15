package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.PaymentRequest;
import org.example.dto.PaymentResponse;
import org.example.dto.RefundRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    
    @GetMapping({"", "/"})
    public ResponseEntity<String> getPayments() {
        return ResponseEntity.ok("Payment API is running");
    }
    
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = new PaymentResponse(
            request.transactionId(),
            "SUCCESS",
            request.amount(),
            request.currency(),
            LocalDateTime.now(),
            "REF" + UUID.randomUUID().toString().substring(0, 8)
        );
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refunds")
    public ResponseEntity<PaymentResponse> processRefund(@Valid @RequestBody RefundRequest request) {
        PaymentResponse response = new PaymentResponse(
            "REFUND" + UUID.randomUUID().toString().substring(0, 8),
            "REFUNDED",
            request.amount(),
            "USD",
            LocalDateTime.now(),
            "REFREF" + UUID.randomUUID().toString().substring(0, 6)
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{transactionId}/status")
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable String transactionId) {
        PaymentResponse response = new PaymentResponse(
            transactionId,
            "COMPLETED",
            new BigDecimal("100.00"),
            "USD",
            LocalDateTime.now(),
            "REF" + UUID.randomUUID().toString().substring(0, 8)
        );
        return ResponseEntity.ok(response);
    }
}