package com.reverse.nsu.repository;

import com.reverse.nsu.entity.ClubProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubProjectRepository extends JpaRepository<ClubProject, Long> {

    // 노출 순서대로 전체 조회
    List<ClubProject> findAllByOrderBySortOrderAsc();
}