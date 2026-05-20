package com.reverse.nsu.dto;

import com.reverse.nsu.entity.StudyStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudyRequestDto {

    private String studyName;
    private String leaderId;
    private String leaderName;
    private String language;
    private String techStack;
    private String description;
    private String goal;
    private String location;
    private String notice;
    private StudyStatus status;
    private List<ScheduleDto> schedules;
    private List<CurriculumDto> curriculums;

    @Getter
    @Setter
    public static class ScheduleDto {
        private Integer dayOfWeek; // 0=일 ~ 6=토
        private String meetTime;   // "18:00" 형태
    }

    @Getter
    @Setter
    public static class CurriculumDto {
        private Integer week;
        private String contents;
    }
}
