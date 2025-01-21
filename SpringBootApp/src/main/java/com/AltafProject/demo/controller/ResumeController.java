package com.AltafProject.demo.controller;

import com.AltafProject.demo.service.ResumeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file) {

        String responseId = resumeService.ProcessResume(file);

        if (responseId.contains("Failed")) {
            return ResponseEntity.badRequest().body(responseId);
        }

        String jsonResponse = "{\"id\": " + responseId + "}";

        return ResponseEntity.ok(jsonResponse);

    }



    @GetMapping("/summary/{id}")
    public ResponseEntity<Map<String, String>>getResumeSummary(@PathVariable Long id) {
        String summary = resumeService.getSummaryById(id);
        String skills = resumeService.getSkillsById(id);


        if (summary == null || summary.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Summary not found for the given ID."));
        } else if (skills == null || skills.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Skills not found for the given ID."));
        }


        Map<String, String> response = new HashMap<>();
        response.put("skills", skills);
        response.put("summary", summary);

        return ResponseEntity.ok(response);


    }




}


