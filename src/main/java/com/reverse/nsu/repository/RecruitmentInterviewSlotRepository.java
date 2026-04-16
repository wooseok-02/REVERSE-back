package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentInterviewSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface RecruitmentInterviewSlotRepository extends JpaRepository<RecruitmentInterviewSlot, Integer> {
    List<RecruitmentInterviewSlot> findAllByRecruitmentIdAndIsActiveTrue(Integer recruitmentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RecruitmentInterviewSlot s WHERE s.recruitmentId = :recruitmentId")
    void deleteByRecruitmentId(@Param("recruitmentId") Integer recruitmentId); // @Param 추가 권장
}