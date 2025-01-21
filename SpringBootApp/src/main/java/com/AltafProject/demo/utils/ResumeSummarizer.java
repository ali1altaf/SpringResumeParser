package com.AltafProject.demo.utils;

import org.springframework.stereotype.Component;

@Component
public class ResumeSummarizer {

    /**
     * Summarizes the resume content.
     *
     * @param resumeContent The text content of the resume.
     * @return A summarized version of the resume.
     */
    public String summarize(String resumeContent) {
        if (resumeContent.length() > 100) {
            return resumeContent.substring(0, 100) + "..."; // Truncate after 1000 characters
        }
        return resumeContent;
    }
}
