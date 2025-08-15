# ExternalAPIIntegrationService
ExternalAPIIntegrationService
Objective:
//Develop a service to integrate with a third-party API
//use production-ready java coding standards, maintainable code

Guideline:
//Create a java spring boot service to integrate with a third-party API with methods for payment processing, refunds, and status checks 
//Include proper HTTP client configuration with timeout, retry logic, response mapping and error handling for API failures
//Implement proper logging for audit trails
//evaluate HTTP client implementation, Error handling and resilience, Response mapping accuracy, Logging and monitoring considerations
//Add to the current context

Deliverables:
//clean and concise executable ready to run application

Summary:
Your API Integration Service is now complete! The service includes:

Core Components:
PaymentController: REST endpoints for payment operations
PaymentApiService: Business logic with retry mechanisms
WebClientConfig: HTTP client configuration with timeouts
DTOs: PaymentRequest, PaymentResponse, RefundRequest with validation
Exception Handling: Custom PaymentApiException

Available Endpoints:
POST /api/v1/payments - Process payments
POST /api/v1/payments/refunds - Process refunds
GET /api/v1/payments/{transactionId}/status - Check payment status

Key Features:
Retry logic with exponential backoff
Timeout configuration
Comprehensive error handling
Request validation
Structured logging
Health monitoring via Actuator

The service is ready to run. You can start it and test the endpoints with your payment provider's API.

set "JAVA_HOME=C:\Program Files\Java\jdk-17" && cmd /c mvnw.cmd clean compile
set "JAVA_HOME=C:\Program Files\Java\jdk-17" && cmd /c mvnw.cmd spring-boot:run

API Endpoints
1. Process Payment
POST http://localhost:8080/api/v1/payments

Input:
{
  "transactionId": "TXN123456",
  "amount": 100.50,
  "currency": "USD",
  "paymentMethod": "CREDIT_CARD"
}

Expected Output:
{
  "transactionId": "TXN123456",
  "status": "SUCCESS",
  "amount": 100.50,
  "currency": "USD",
  "timestamp": "2024-01-15T10:30:00",
  "referenceId": "REF789012"
}

2. Process Refund
POST http://localhost:8080/api/v1/payments/refunds

Input:
{
  "originalTransactionId": "TXN123456",
  "amount": 50.25,
  "reason": "Customer request"
}


Expected Output:
{
  "transactionId": "REFUND789",
  "status": "REFUNDED",
  "amount": 50.25,
  "currency": "USD",
  "timestamp": "2024-01-15T11:00:00",
  "referenceId": "REFREF456"
}


3. Check Payment Status
GET http://localhost:8080/api/v1/payments/{transactionId}/status

Input: Path parameter transactionId (e.g., TXN123456)

Expected Output:
{
  "transactionId": "TXN123456",
  "status": "COMPLETED",
  "amount": 100.50,
  "currency": "USD",
  "timestamp": "2024-01-15T10:30:00",
  "referenceId": "REF789012"
}

All endpoints return HTTP status codes matching the external API errors (400, 401, 500, etc.) with empty body on failure.

Current Logging & Monitoring Setup
Your application already has basic logging and monitoring configured:

Logging Configuration (application.yml)
Log Level : INFO for your app, DEBUG for WebClient

Pattern : Includes timestamp, thread, level, traceId, logger, and message
WebClient Debug : Enabled to see HTTP request/response details

Monitoring Endpoints (Spring Actuator)
Health : /actuator/health
Info : /actuator/info
Metrics : /actuator/metrics

How to Check Logging & Monitoring
1. View Application Logs
# Run the application and check console output
mvn spring-boot:run
# Or check log files if configured
tail -f logs/application.log

2. Monitor Health & Metrics
# Health check
curl http://localhost:8080/actuator/health
# Application metrics
curl http://localhost:8080/actuator/metrics
# Specific metric (e.g., HTTP requests)
curl http://localhost:8080/actuator/metrics/http.server.requests

3. WebClient Request Logging
With DEBUG level enabled for WebClient, you'll see:
Request URLs, headers, body
Response status, headers, body
Connection timeouts and errors

4. Payment Service Logs
Your service already logs:
Transaction start/success/failure
HTTP errors with status codes
Retry attempts
Processing times

5. Enhanced Monitoring (Optional)
Add to application.yml for more detailed monitoring:

Screenshot document is available in src\main\resources\document folder