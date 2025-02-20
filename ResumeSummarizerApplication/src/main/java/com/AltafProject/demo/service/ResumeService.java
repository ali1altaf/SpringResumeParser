package com.AltafProject.demo.service;

import com.AltafProject.demo.model.Resume; // Resume entity class representing database records
import com.AltafProject.demo.repository.ResumeRepository; // JPA repository for database operations
import com.AltafProject.demo.utils.ResumeContentExtractor; // Utility class for extracting resume content
import org.springframework.beans.factory.annotation.Autowired; // Used for dependency injection
import org.springframework.stereotype.Service; // Marks this class as a service in Spring
import org.springframework.web.multipart.MultipartFile; // Represents uploaded files in a Spring application

import java.io.IOException; // Handles file IO exceptions
import java.util.Map; // Represents data in key-value pairs

@Service // Marks this class as a Spring-managed service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository; // Repository for saving and retrieving Resume objects

    @Autowired
    private ResumeContentExtractor contentExtractor; // Utility for extracting text from resumes

    @Autowired
    private GeminiIntegrationService GeminiIntegrationService; // Service for integrating with Flask API

    /**
     * Processes the content of a resume by sending it to the Flask API for analysis.
     * @param resumeContent The content of the resume as a string.
     * @return A map containing the extracted summary and skills.
     */
    public Map<String, String> processResumeContent(String resumeContent,String jobDescription) {
        // Calls the Flask API for resume summary and skills extraction
        return GeminiIntegrationService.getResumeSummaryAndSkills(resumeContent,jobDescription);
    }

    /**
     * Processes the uploaded resume file.
     * @param file The resume file uploaded by the user.
     * @return The generated ID of the saved resume in the database or an error message if processing fails.
     */
    public String ProcessResume(MultipartFile file,String jobDescription) {
        try {
            // Get the original file name from the uploaded file
            String fileName = file.getOriginalFilename();

            // Extract the content of the resume using the utility
            String resumeContent = contentExtractor.extractContent(file);

            // Send the extracted content to the Flask API to get skills and summary
            Map<String, String> GeminiResponse =processResumeContent(resumeContent,jobDescription);
            String skills = GeminiResponse.get("skills");
            String summary = GeminiResponse.get("summary");
            String ats_match = GeminiResponse.get("ats_match");

            // Create a new Resume entity to save in the database
            Resume resume = new Resume();
            resume.setFileName(fileName); // Set the file name
            resume.setSkills(skills); // Set the extracted skills
            resume.setSummary(summary); // Set the extracted summary
            resume.setATS_SCORE(ats_match); // Set the extracted summary

            // Save the resume to the database and retrieve the saved entity
            Resume savedResume = resumeRepository.save(resume);

            // Get the generated ID of the saved resume
            Long generatedId = savedResume.getId();

            // Return the generated ID as a string
            return generatedId.toString();

        } catch (IOException e) {
            // Handle exceptions during file processing and return an error message
            e.printStackTrace();
            return "Failed to process the resume.";
        }
    }

    /**
     * Retrieves the summary of a resume by its ID.
     * @param id The ID of the resume.
     * @return The summary of the resume or an error message if the resume is not found.
     */
    public String getSummaryById(Long id) {
        // Use the repository to find the resume by ID and retrieve its summary
        return resumeRepository.findById(id)
                .map(Resume::getSummary) // Get the summary if the resume is found
                .orElse("Summary not found for the given ID."); // Return error if not found
    }

    /**
     * Retrieves the skills of a resume by its ID.
     * @param id The ID of the resume.
     * @return The skills of the resume or an error message if the resume is not found.
     */
    public String getSkillsById(Long id) {
        // Use the repository to find the resume by ID and retrieve its skills
        return resumeRepository.findById(id)
                .map(Resume::getSkills) // Get the skills if the resume is found
                .orElse("Summary not found for the given ID."); // Return error if not found
    }

    public String getATSById(Long id) {
        // Use the repository to find the resume by ID and retrieve its skills
        return resumeRepository.findById(id)
                .map(Resume::getATS) // Get the skills if the resume is found
                .orElse("Summary not found for the given ID."); // Return error if not found
    }
}
