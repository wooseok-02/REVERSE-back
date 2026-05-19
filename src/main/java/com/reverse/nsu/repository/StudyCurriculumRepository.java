package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Study;
import com.reverse.nsu.entity.StudyCurriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyCurriculumRepository extends JpaRepository<StudyCurriculum, Integer> {

    List<StudyCurriculum> findAllByStudyOrderByWeekAsc(Study study);

    void deleteAllByStudy(Study study);
}
