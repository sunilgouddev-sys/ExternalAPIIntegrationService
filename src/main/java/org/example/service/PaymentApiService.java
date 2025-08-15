package org.example.service;

import org.example.dto.PaymentRequest;
import org.example.dto.PaymentResponse;
import org.example.dto.RefundRequest;
import org.example.exception.PaymentApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class PaymentApiService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentApiService.class);
    
    private final WebClient webClient;
    
    @Value("${payment.api.key:your-api-key}")
    private String apiKey;
    
    public PaymentApiService(WebClient paymentWebClient) {
        this.webClient = paymentWebClient;
    }
    
    @Retryable(retryFor = {PaymentApiException.class}, maxAttempts = 3, 
               backoff = @Backoff(delay = 1000, multiplier = 2))
    public PaymentResponse processPayment(PaymentRequest request) {
        logger.info("Processing payment for transaction: {}", request.transactionId());
        
        try {
            return webClient.post()
                    .uri("/payments")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new PaymentApiException("Payment failed: " + body, response.statusCode().value())))
                    .bodyToMono(PaymentResponse.class)
                    .timeout(Duration.ofSeconds(30))
                    .doOnSuccess(response -> logger.info("Payment processed successfully: {}", response.transactionId()))
                    .doOnError(error -> logger.error("Payment processing failed for transaction: {}", request.transactionId(), error))
                    .block();
        } catch (WebClientResponseException e) {
            logger.error("HTTP error during payment processing: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new PaymentApiException("Payment API error: " + e.getMessage(), e, e.getStatusCode().value());
        } catch (Exception e) {
            logger.error("Unexpected error during payment processing", e);
            throw new PaymentApiException("Payment processing failed", e, 500);
        }
    }
    
    @Retryable(retryFor = {PaymentApiException.class}, maxAttempts = 3,
               backoff = @Backoff(delay = 1000, multiplier = 2))
    public PaymentResponse processRefund(RefundRequest request) {
        logger.info("Processing refund for transaction: {}", request.originalTransactionId());
        
        try {
            return webClient.post()
                    .uri("/refunds")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new PaymentApiException("Refund failed: " + body, response.statusCode().value())))
                    .bodyToMono(PaymentResponse.class)
                    .timeout(Duration.ofSeconds(30))
                    .doOnSuccess(response -> logger.info("Refund processed successfully: {}", response.transactionId()))
                    .doOnError(error -> logger.error("Refund processing failed for transaction: {}", request.originalTransactionId(), error))
                    .block();
        } catch (WebClientResponseException e) {
            logger.error("HTTP error during refund processing: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new PaymentApiException("Refund API error: " + e.getMessage(), e, e.getStatusCode().value());
        } catch (Exception e) {
            logger.error("Unexpected error during refund processing", e);
            throw new PaymentApiException("Refund processing failed", e, 500);
        }
    }
    
    @Retryable(retryFor = {PaymentApiException.class}, maxAttempts = 2,
               backoff = @Backoff(delay = 500))
    public PaymentResponse getPaymentStatus(String transactionId) {
        logger.info("Checking payment status for transaction: {}", transactionId);
        
        try {
            return webClient.get()
                    .uri("/payments/{transactionId}/status", transactionId)
                    .header("Authorization", "Bearer " + apiKey)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new PaymentApiException("Status check failed: " + body, response.statusCode().value())))
                    .bodyToMono(PaymentResponse.class)
                    .timeout(Duration.ofSeconds(15))
                    .doOnSuccess(response -> logger.info("Status retrieved for transaction: {} - Status: {}", transactionId, response.status()))
                    .doOnError(error -> logger.error("Status check failed for transaction: {}", transactionId, error))
                    .block();
        } catch (WebClientResponseException e) {
            logger.error("HTTP error during status check: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new PaymentApiException("Status API error: " + e.getMessage(), e, e.getStatusCode().value());
        } catch (Exception e) {
            logger.error("Unexpected error during status check", e);
            throw new PaymentApiException("Status check failed", e, 500);
        }
    }
}