package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentInterviewSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecruitmentInterviewSlotRepository extends JpaRepository<RecruitmentInterviewSlot, Integer> {
    // 공고 ID로 모든 면접 슬롯을 찾아오는 메서드 추가
    List<RecruitmentInterviewSlot> findAllByRecruitment_RecruitmentId(Integer recruitmentId);
}