package com.AltafProject.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name="RESUME_DETAILS")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 30)
    private String fileName;
    @Column(length = 1000)
    private String skills;
    @Column(length = 2000)
    private String summary;

    public Resume() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Resume(Long id, String fileName, String skills, String summary) {
        this.id = id;
        this.fileName = fileName;
        this.skills = skills;
        this.summary = summary;
    }


}
