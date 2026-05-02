package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPageFieldCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface RecruitmentPageFieldCardRepository extends JpaRepository<RecruitmentPageFieldCard, Integer> {

    // 1. 페이지 ID로 모집 분야 카드 목록 조회 (정렬 순서 반영)
    List<RecruitmentPageFieldCard> findAllByPage_PageIdOrderBySortOrderAsc(Integer pageId);

    // 2. 페이지 ID로 해당 분야 카드 데이터 모두 삭제 (초기화 후 재등록 시 사용)
    @Modifying
    @Transactional
    void deleteByPage_PageId(Integer pageId);
}