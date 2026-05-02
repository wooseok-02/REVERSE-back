package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPageContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface RecruitmentPageContactRepository extends JpaRepository<RecruitmentPageContact, Integer> {

    // 1. 페이지 ID로 연락처 목록 조회 (정렬 순서 포함)
    List<RecruitmentPageContact> findAllByPage_PageIdOrderBySortOrderAsc(Integer pageId);

    // 2. 페이지 ID로 관련 연락처 모두 삭제 (페이지 수정 시 기존 데이터 초기화 용도)
    @Modifying
    @Transactional
    void deleteByPage_PageId(Integer pageId);
}