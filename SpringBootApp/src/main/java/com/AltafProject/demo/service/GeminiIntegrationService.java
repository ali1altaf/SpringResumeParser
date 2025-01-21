package com.AltafProject.demo.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GeminiIntegrationService {

    private final String FLASK_API_URL = "http://localhost:5001/extract-summary";

    public Map<String, String> getResumeSummaryAndSkills(String resumeContent) {
        RestTemplate restTemplate = new RestTemplate();

        // Create request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Create request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("resume_content", resumeContent);

        // Send POST request to Flask API
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                FLASK_API_URL,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        // Return response as a Map
        return (Map<String, String>) response.getBody();
    }
}
