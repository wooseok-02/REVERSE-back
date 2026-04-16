package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPageContact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecruitmentPageContactRepository extends JpaRepository<RecruitmentPageContact, Integer> {
    List<RecruitmentPageContact> findAllByPageIdOrderBySortOrderAsc(Integer pageId);
    void deleteByPageId(Integer pageId);
}