package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPageGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface RecruitmentPageGalleryRepository extends JpaRepository<RecruitmentPageGallery, Integer> {

    // 1. [기존] 페이지 전체 사진 조회
    List<RecruitmentPageGallery> findAllByRecruitmentPage_PageIdOrderBySortOrderAsc(Integer pageId);

    // 2. [추가] 특정 태그(분야)별 사진 필터링 조회 (화면정의서 핵심 기능)
    // 예: tag가 '백엔드'인 사진들만 정렬해서 가져옴
    List<RecruitmentPageGallery> findAllByRecruitmentPage_PageIdAndTagOrderBySortOrderAsc(Integer pageId, String tag);

    // 3. [보완] 삭제 메서드에 필요한 어노테이션 추가
    @Modifying
    @Transactional
    void deleteByRecruitmentPage_PageId(Integer pageId);
}