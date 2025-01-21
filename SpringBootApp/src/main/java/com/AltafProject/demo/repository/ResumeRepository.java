package com.AltafProject.demo.repository;

import com.AltafProject.demo.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

}

