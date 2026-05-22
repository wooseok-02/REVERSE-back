package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Project;
import com.reverse.nsu.entity.ProjectSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProjectScheduleRepository extends JpaRepository<ProjectSchedule, Integer> {

    /**
     * 특정 프로젝트에 등록된 모든 진행 일정(요일/시간) 조회
     */
    List<ProjectSchedule> findAllByProject(Project project);

    /**
     * 특정 프로젝트의 기존 일정 전체 삭제 (수정 시 활용)
     * - 🔥 [수정] 벌크성 삭제 마크(@Modifying)와 트랜잭션 보장 선언을 추가하여 안전성을 확보합니다.
     */
    @Modifying
    @Transactional
    void deleteAllByProject(Project project);

}