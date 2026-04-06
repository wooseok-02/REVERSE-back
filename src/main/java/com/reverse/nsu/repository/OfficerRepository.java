package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Officer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfficerRepository extends JpaRepository<Officer, Long> {

    // 기수별, 노출 순서대로 조회
    List<Officer> findAllByIsVisibleTrueOrderByGenerationDescSortOrderAsc();
}