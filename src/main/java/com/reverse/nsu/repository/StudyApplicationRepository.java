package com.reverse.nsu.repository;

import com.reverse.nsu.entity.ApplicationStatus;
import com.reverse.nsu.entity.Study;
import com.reverse.nsu.entity.StudyApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyApplicationRepository extends JpaRepository<StudyApplication, Integer> {

    List<StudyApplication> findAllByStudyAndStatus(Study study, ApplicationStatus status);

    boolean existsByStudyAndUserIdAndStatus(Study study, String userId, ApplicationStatus status);

    void deleteAllByUserId(String userId);
}
