package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentInterviewSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecruitmentInterviewSlotRepository extends JpaRepository<RecruitmentInterviewSlot, Integer> {
    // 특정 공고의 모든 면접 슬롯 조회 (활성화 상태 필터링)
    List<RecruitmentInterviewSlot> findAllByRecruitment_RecruitmentIdAndIsActiveTrue(Integer recruitmentId);
}