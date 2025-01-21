package com.AltafProject.demo.service;

import com.AltafProject.demo.model.Resume;
import com.AltafProject.demo.repository.ResumeRepository;
import com.AltafProject.demo.utils.ResumeContentExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ResumeContentExtractor contentExtractor;
    @Autowired
    private GeminiIntegrationService GeminiIntegrationService;


    public Map<String, String> processResumeContent(String resumeContent) {
        // Call Flask service for extracting summary and skills

        return GeminiIntegrationService.getResumeSummaryAndSkills(resumeContent);
    }



    public String ProcessResume(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();


            String resumeContent = contentExtractor.extractContent(file);


            String skills = processResumeContent(resumeContent).get("skills");
            String summary = processResumeContent(resumeContent).get("summary");

            // Create a Resume entity and save it to the database
            Resume resume = new Resume();
            resume.setFileName(fileName);
            resume.setSkills(skills.toString());
            resume.setSummary(summary);

            // Save the resume and get the saved entity
            Resume savedResume = resumeRepository.save(resume);

            // Get the generated ID
            Long generatedId = savedResume.getId();

            return generatedId.toString();


        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to process the resume.";
        }

    }

    public String getSummaryById(Long id) {
        return resumeRepository.findById(id)
                .map(resume -> resume.getSummary())
                .orElse("Summary not found for the given ID.");

    }

    public String getSkillsById(Long id) {
        return resumeRepository.findById(id)
                .map(resume -> resume.getSkills())
                .orElse("Summary not found for the given ID.");

    }



}



