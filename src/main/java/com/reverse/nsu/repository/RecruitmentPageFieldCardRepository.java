package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPageFieldCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecruitmentPageFieldCardRepository extends JpaRepository<RecruitmentPageFieldCard, Integer> {
    List<RecruitmentPageFieldCard> findAllByPageIdOrderBySortOrderAsc(Integer pageId);

    void deleteByPageId(Integer pageId);
}