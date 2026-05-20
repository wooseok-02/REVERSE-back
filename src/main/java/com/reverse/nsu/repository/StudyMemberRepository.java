package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Study;
import com.reverse.nsu.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyMemberRepository extends JpaRepository<StudyMember, Integer> {

    List<StudyMember> findAllByStudy(Study study);

    boolean existsByStudyAndUserId(Study study, String userId);

    Optional<StudyMember> findByStudyAndUserId(Study study, String userId);
}
