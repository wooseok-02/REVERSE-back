package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Study;
import com.reverse.nsu.entity.StudySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyScheduleRepository extends JpaRepository<StudySchedule, Integer> {

    List<StudySchedule> findAllByStudy(Study study);

    void deleteAllByStudy(Study study);
}
