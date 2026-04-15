package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    // 특정 월에 걸치는 일정 조회 (공개용 - isVisible=true만)
    @Query("SELECT s FROM Schedule s WHERE s.isVisible = true " +
           "AND s.startDate <= :lastDay AND s.endDate >= :firstDay " +
           "ORDER BY s.startDate ASC")
    List<Schedule> findVisibleByMonth(@Param("firstDay") LocalDate firstDay,
                                      @Param("lastDay") LocalDate lastDay);

    // 특정 월에 걸치는 일정 조회 (관리자용 - 모든 일정)
    @Query("SELECT s FROM Schedule s WHERE " +
           "s.startDate <= :lastDay AND s.endDate >= :firstDay " +
           "ORDER BY s.startDate ASC")
    List<Schedule> findAllByMonth(@Param("firstDay") LocalDate firstDay,
                                  @Param("lastDay") LocalDate lastDay);
}