package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(scanBasePackages = "org.example")
@EnableRetry
public class ApiIntegrationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiIntegrationServiceApplication.class, args);
    }
}