package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentInterviewSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;

public interface RecruitmentInterviewSlotRepository extends JpaRepository<RecruitmentInterviewSlot, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select slot from RecruitmentInterviewSlot slot where slot.slotId = :slotId")
    Optional<RecruitmentInterviewSlot> findByIdForUpdate(@Param("slotId") Integer slotId);
    List<RecruitmentInterviewSlot> findAllByRecruitment_RecruitmentId(Integer recruitmentId);

    @Modifying
    void deleteByRecruitment_RecruitmentId(Integer recruitmentId);
}
