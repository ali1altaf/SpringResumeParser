package com.AltafProject.demo.utils;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ResumeSkillsExtractor {

    public String extractSkills(String content) {
        // List of common patterns for skills (can be extended further)
        List<String> skillPatterns = Arrays.asList(
                "Java", "Spring", "SQL", "Python", "JavaScript", "AWS", "Docker", "Machine Learning",
                "React", "Node.js", "Angular", "HTML", "CSS", "Git", "Kubernetes", "C++", "C#",
                "Ruby", "Scala", "TypeScript", "PHP", "Go", "Cloud", "API", "SQL", "NoSQL"
        );

        Set<String> extractedSkills = new HashSet<>();

        // Loop through each skill pattern and find matches in the content
        for (String pattern : skillPatterns) {
            Pattern regex = Pattern.compile("\\b" + Pattern.quote(pattern) + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = regex.matcher(content);

            while (matcher.find()) {
                extractedSkills.add(matcher.group().toLowerCase()); // Store matched skill in lowercase
            }
        }

        // Convert the Set to a comma-separated string

        return String.join(", ", extractedSkills);
    }
}
