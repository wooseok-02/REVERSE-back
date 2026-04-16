package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ScheduleRequestDto {
    private Integer categoryId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAllDay;
    private Boolean isVisible;
    private String updatedBy;
}
