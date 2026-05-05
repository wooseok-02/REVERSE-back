package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPageIntro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface RecruitmentPageIntroRepository extends JpaRepository<RecruitmentPageIntro, Integer> {
    // 메서드 이름이 정확히 일치해야 합니다.
    List<RecruitmentPageIntro> findAllByRecruitmentPage_PageIdOrderBySortOrderAsc(Integer pageId);

    // delete 로직을 위해 이것도 필요합니다.
    void deleteByRecruitmentPage_PageId(Integer pageId);
}