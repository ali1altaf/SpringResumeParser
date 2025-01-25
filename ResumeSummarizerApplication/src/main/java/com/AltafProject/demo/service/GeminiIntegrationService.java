package com.AltafProject.demo.service;

import org.springframework.beans.factory.annotation.Value; // Annotation to inject values from properties files
import org.springframework.http.HttpEntity; // Represents the HTTP request or response entity
import org.springframework.http.HttpHeaders; // Represents HTTP headers
import org.springframework.http.HttpMethod; // Enum representing HTTP methods like GET, POST
import org.springframework.http.ResponseEntity; // Represents the HTTP response entity
import org.springframework.stereotype.Service; // Marks this class as a service component in Spring
import org.springframework.web.client.RestTemplate; // Used for making HTTP requests

import java.util.HashMap;
import java.util.Map;

@Service // Marks this class as a Spring service component, which can be injected where needed
public class GeminiIntegrationService {

    // The Flask API URL for integration, injected from the properties file
    @Value("${gemini.api.url}") // Reads the value of "gemini.api.url" from application_properties.properties
    private String FLASK_API_URL;

    // The API key for authenticating with the Flask API, injected from the properties file
    @Value("${gemini.api.key}") // Reads the value of "gemini.api.key" from application_properties.properties
    private String geminiApiKey;

    /**
     * Getter for the Gemini API Key.
     * @return The API key used for authentication with the Flask API.
     */
    public String getGeminiApiKey() {
        return geminiApiKey;
    }

    /**
     * Getter for the Flask API URL.
     * @return The Flask API endpoint URL.
     */
    public String getGeminiApiUrl() {
        return FLASK_API_URL;
    }

    /**
     * Method to send a request to the Flask API and get the resume summary and skills.
     * @param resumeContent The content of the resume to be analyzed.
     * @return A map containing the extracted summary and skills.
     */
    public Map<String, String> getResumeSummaryAndSkills(String resumeContent) {
        // Create an instance of RestTemplate for making HTTP requests
        RestTemplate restTemplate = new RestTemplate();

        // Create HTTP headers for the request
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json"); // Set the content type to JSON

        // Create the request body as a map
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("resume_content", resumeContent); // Add the resume content to the request body
        requestBody.put("api_key", getGeminiApiKey()); // Add the API key to the request body

        // Wrap the headers and body into an HttpEntity object
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send a POST request to the Flask API
        ResponseEntity<Map> response = restTemplate.exchange(
                getGeminiApiUrl(), // The URL of the Flask API
                HttpMethod.POST, // The HTTP method (POST)
                requestEntity, // The request entity containing the headers and body
                Map.class // The expected response type (a Map in this case)
        );

        // Return the response body as a Map (contains the summary and skills)
        return (Map<String, String>) response.getBody();
    }
}
