package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScheduleResponseDto {
    private Integer id;
    private Integer categoryId;
    private String categoryName;
    private String colorCode;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAllDay;
    private Boolean isVisible;
    private String updatedBy;
    private String createdAt;
    private String updatedAt;

    public static ScheduleResponseDto from(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .categoryId(schedule.getCategory().getId())
                .categoryName(schedule.getCategory().getCategoryName())
                .colorCode(schedule.getCategory().getColorCode())
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .isAllDay(schedule.getIsAllDay())
                .isVisible(schedule.getIsVisible())
                .updatedBy(schedule.getUpdatedBy())
                .createdAt(schedule.getCreatedAt() != null ? schedule.getCreatedAt().toString() : "")
                .updatedAt(schedule.getUpdatedAt() != null ? schedule.getUpdatedAt().toString() : "")
                .build();
    }
}
