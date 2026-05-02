package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPageGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface RecruitmentPageGalleryRepository extends JpaRepository<RecruitmentPageGallery, Integer> {

    // 1. 페이지 ID로 전체 갤러리 목록 조회 (정렬 순서 반영)
    List<RecruitmentPageGallery> findAllByPage_PageIdOrderBySortOrderAsc(Integer pageId);

    // 2. 페이지 ID로 '노출 설정된(isVisible=true)' 갤러리만 조회
    List<RecruitmentPageGallery> findAllByPage_PageIdAndIsVisibleTrueOrderBySortOrderAsc(Integer pageId);

    // 3. 페이지 ID로 해당 갤러리 데이터 모두 삭제
    @Modifying
    @Transactional
    void deleteByPage_PageId(Integer pageId);
}