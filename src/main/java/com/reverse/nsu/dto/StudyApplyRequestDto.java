package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudyApplyRequestDto {

    private List<AvailabilityDto> availabilities;

    @Getter
    @Setter
    public static class AvailabilityDto {
        private Integer dayOfWeek;    // 0=일 ~ 6=토
        private String availableTime; // "18:00" 형태
    }
}
