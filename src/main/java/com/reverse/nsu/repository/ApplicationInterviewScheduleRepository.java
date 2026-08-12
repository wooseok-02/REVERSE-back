package com.reverse.nsu.repository;

import com.reverse.nsu.entity.ApplicationInterviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationInterviewScheduleRepository extends JpaRepository<ApplicationInterviewSchedule, Integer> {
    long countByInterviewSlot_SlotId(Integer slotId);
}
