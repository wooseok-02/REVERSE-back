package com.reverse.nsu.dto;

import com.reverse.nsu.entity.ApplicationStatus;
import com.reverse.nsu.entity.StudyApplication;
import com.reverse.nsu.entity.StudyApplicationAvailability;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StudyApplicationResponseDto {

    private final Integer studyApplicationId;
    private final Integer studyId;
    private final String userId;
    private final ApplicationStatus status;
    private final LocalDateTime appliedDate;
    private final List<AvailabilityDto> availabilities;

    public StudyApplicationResponseDto(StudyApplication application) {
        this.studyApplicationId = application.getStudyApplicationId();
        this.studyId = application.getStudy().getStudyId();
        this.userId = application.getUserId();
        this.status = application.getStatus();
        this.appliedDate = application.getAppliedDate();
        this.availabilities = application.getAvailabilities().stream()
                .map(AvailabilityDto::new)
                .collect(Collectors.toList());
    }

    @Getter
    public static class AvailabilityDto {
        private final Integer dayOfWeek;
        private final String availableTime;

        public AvailabilityDto(StudyApplicationAvailability availability) {
            this.dayOfWeek = availability.getDayOfWeek();
            this.availableTime = availability.getAvailableTime().toString();
        }
    }
}
