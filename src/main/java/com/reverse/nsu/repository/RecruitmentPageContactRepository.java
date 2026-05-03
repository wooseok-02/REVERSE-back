package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPageContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface RecruitmentPageContactRepository extends JpaRepository<RecruitmentPageContact, Integer> {
    List<RecruitmentPageContact> findAllByRecruitmentPage_PageIdOrderBySortOrderAsc(Integer pageId);
    void deleteByRecruitmentPage_PageId(Integer pageId);
}