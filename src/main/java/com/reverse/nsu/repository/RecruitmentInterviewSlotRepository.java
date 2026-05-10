package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentInterviewSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;

public interface RecruitmentInterviewSlotRepository extends JpaRepository<RecruitmentInterviewSlot, Integer> {
    List<RecruitmentInterviewSlot> findAllByRecruitment_RecruitmentId(Integer recruitmentId);

    @Modifying
    void deleteByRecruitment_RecruitmentId(Integer recruitmentId);
}