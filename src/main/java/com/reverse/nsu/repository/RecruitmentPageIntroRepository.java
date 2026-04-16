package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPageIntro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecruitmentPageIntroRepository extends JpaRepository<RecruitmentPageIntro, Integer> {
    // pageId로 조회하되, 순서(sortOrder)대로 정렬해서 가져옴
    List<RecruitmentPageIntro> findAllByPageIdOrderBySortOrderAsc(Integer pageId);
    void deleteByPageId(Integer pageId);
}