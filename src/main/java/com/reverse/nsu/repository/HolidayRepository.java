package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Integer> {
    List<Holiday> findByHolidayDateBetweenOrderByHolidayDateAsc(LocalDate from, LocalDate to);

    @Modifying
    @Query("DELETE FROM Holiday h WHERE h.year = :year")
    void deleteByYear(@Param("year") Short year);
}
