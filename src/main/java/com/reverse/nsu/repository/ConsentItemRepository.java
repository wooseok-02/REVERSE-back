package com.reverse.nsu.repository;

import com.reverse.nsu.entity.ConsentItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsentItemRepository extends JpaRepository<ConsentItem, Integer> {
    List<ConsentItem> findAllByIsActiveTrueOrderBySortOrderAsc();
}
