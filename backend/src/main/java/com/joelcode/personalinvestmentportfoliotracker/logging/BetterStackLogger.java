package com.joelcode.personalinvestmentportfoliotracker.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "BETTERSTACK_TOKEN")
public class BetterStackLogger {

    @Value("${BETTERSTACK_TOKEN}")
    private String token;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BETTERSTACK_URL = "https://s1622070.eu-nbg-2.betterstackdata.com";

    public void log(String message, String level) {
        Map<String, Object> body = new HashMap<>();
        body.put("dt", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
        body.put("message", message);
        body.put("level", level);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(BETTERSTACK_URL, request, String.class);
        } catch (Exception e) {
            System.err.println("Failed to send log to Better Stack: " + e.getMessage());
        }
    }

    public void info(String message) {
        log(message, "INFO");
    }

    public void error(String message) {
        log(message, "ERROR");
    }

    public void warn(String message) {
        log(message, "WARN");
    }
}
