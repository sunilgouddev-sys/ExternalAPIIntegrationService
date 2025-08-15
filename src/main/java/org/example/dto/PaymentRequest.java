package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PaymentRequest(
    @NotBlank String transactionId,
    @NotNull @Positive BigDecimal amount,
    @NotBlank String currency,
    @NotBlank String paymentMethod
) {}