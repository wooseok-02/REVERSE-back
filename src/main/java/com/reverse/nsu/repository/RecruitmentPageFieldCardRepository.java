package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPageFieldCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface RecruitmentPageFieldCardRepository extends JpaRepository<RecruitmentPageFieldCard, Integer> {
    List<RecruitmentPageFieldCard> findAllByRecruitmentPage_PageIdOrderBySortOrderAsc(Integer pageId);

    void deleteByRecruitmentPage_PageId(Integer pageId);
}