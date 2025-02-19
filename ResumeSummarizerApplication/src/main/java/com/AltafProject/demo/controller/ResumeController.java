package com.AltafProject.demo.controller;

import com.AltafProject.demo.service.ResumeService; // Service layer for business logic
import com.fasterxml.jackson.databind.ObjectMapper; // Used for JSON operations (not directly used in this code)
import org.springframework.beans.factory.annotation.Autowired; // Enables dependency injection
import org.springframework.http.HttpStatus; // Represents HTTP status codes
import org.springframework.http.ResponseEntity; // Wraps HTTP responses with status and body
import org.springframework.web.bind.annotation.*; // For creating RESTful APIs
import org.springframework.web.multipart.MultipartFile; // Handles file uploads

import java.util.Collections; // Provides utility methods for creating immutable maps
import java.util.HashMap; // Map implementation for storing key-value pairs
import java.util.Map; // Interface representing a key-value mapping

// Marks this class as a REST controller and allows handling HTTP requests
@RestController
@RequestMapping("/api/resumes") // Base URL for all endpoints in this controller
public class ResumeController {

    // Automatically injects the ResumeService instance (service layer)
    @Autowired
    private ResumeService resumeService;

    /**
     * Endpoint for uploading a resume file.
     * @param file The uploaded file received as a MultipartFile.
     * @return A ResponseEntity containing a JSON response with the resume ID or an error message.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file,@RequestParam("jobDescription") String jobDescription) {
        System.out.println(jobDescription);
        // Calls the service method to process the uploaded resume and returns an ID or failure message

        String responseId = resumeService.ProcessResume(file);

        // If processing failed, return a bad request response with the error message
        if (responseId.contains("Failed")) {
            return ResponseEntity.badRequest().body(responseId);
        }

        // Creates a JSON response with the ID returned from the service layer
        String jsonResponse = "{\"id\": " + responseId + "}";

        // Returns the response with HTTP 200 (OK) status and the ID in JSON format
        return ResponseEntity.ok(jsonResponse);
    }

    /**
     * Endpoint for fetching the summary and skills for a resume by its ID.
     * @param id The ID of the resume to retrieve data for.
     * @return A ResponseEntity containing a map of summary and skills, or an error message.
     */
    @GetMapping("/summary/{id}")
    public ResponseEntity<Map<String, String>> getResumeSummary(@PathVariable Long id) {
        // Fetches the summary and skills for the given resume ID from the service layer
        String summary = resumeService.getSummaryById(id);
        String skills = resumeService.getSkillsById(id);

        // If no summary is found for the given ID, return a 404 (Not Found) response with an error message
        if (summary == null || summary.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Summary not found for the given ID."));
        }
        // If no skills are found for the given ID, return a 404 (Not Found) response with an error message
        else if (skills == null || skills.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Skills not found for the given ID."));
        }

        // Creates a map containing the skills and summary to be returned in the response
        Map<String, String> response = new HashMap<>();
        response.put("skills", skills);
        response.put("summary", summary);

        // Returns the response with HTTP 200 (OK) status and the map containing the data
        return ResponseEntity.ok(response);
    }
}
