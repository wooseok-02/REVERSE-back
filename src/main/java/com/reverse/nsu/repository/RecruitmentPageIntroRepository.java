package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPageIntro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface RecruitmentPageIntroRepository extends JpaRepository<RecruitmentPageIntro, Integer> {

    // 1. 페이지 ID로 소개 글 목록 조회 (정렬 순서 반영)
    List<RecruitmentPageIntro> findAllByPage_PageIdOrderBySortOrderAsc(Integer pageId);

    // 2. 페이지 ID로 해당 소개 글들 모두 삭제
    @Modifying
    @Transactional
    void deleteByPage_PageId(Integer pageId);
}