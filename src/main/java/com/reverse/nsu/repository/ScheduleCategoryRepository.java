package com.reverse.nsu.repository;

import com.reverse.nsu.entity.ScheduleCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleCategoryRepository extends JpaRepository<ScheduleCategory, Integer> {
    List<ScheduleCategory> findAllByOrderBySortOrderAsc();
    List<ScheduleCategory> findByIsVisibleTrueOrderBySortOrderAsc();
}