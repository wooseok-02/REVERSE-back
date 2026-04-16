package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPageGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecruitmentPageGalleryRepository extends JpaRepository<RecruitmentPageGallery, Integer> {
    // 노출 여부(isVisible)가 true인 것만 가져오고 싶다면 메서드명을 바꿀 수도 있습니다.
    List<RecruitmentPageGallery> findAllByPageIdOrderBySortOrderAsc(Integer pageId);
    void deleteByPageId(Integer pageId);
}