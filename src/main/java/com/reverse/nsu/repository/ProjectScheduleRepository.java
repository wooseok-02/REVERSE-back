package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Project;
import com.reverse.nsu.entity.ProjectSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectScheduleRepository extends JpaRepository<ProjectSchedule, Integer> {

    /**
     * 특정 프로젝트에 등록된 모든 진행 일정(요일/시간) 조회
     */
    List<ProjectSchedule> findAllByProject(Project project);

    /**
     * 특정 프로젝트의 기존 일정 전체 삭제 (수정 시 유용)
     */
    void deleteAllByProject(Project project);
}