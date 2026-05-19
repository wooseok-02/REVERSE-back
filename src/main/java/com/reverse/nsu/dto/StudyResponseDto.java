package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Study;
import com.reverse.nsu.entity.StudyStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StudyResponseDto {

    private final Integer studyId;
    private final String studyName;
    private final String leaderId;
    private final String leaderName;
    private final String language;
    private final String techStack;
    private final String description;
    private final String goal;
    private final Integer memberCount;
    private final String location;
    private final String notice;
    private final StudyStatus status;
    private final String createdBy;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;
    private final List<ScheduleDto> schedules;
    private final List<CurriculumDto> curriculums;

    public StudyResponseDto(Study study) {
        this.studyId = study.getStudyId();
        this.studyName = study.getStudyName();
        this.leaderId = study.getLeaderId();
        this.leaderName = study.getLeaderName();
        this.language = study.getLanguage();
        this.techStack = study.getTechStack();
        this.description = study.getDescription();
        this.goal = study.getGoal();
        this.memberCount = study.getMemberCount();
        this.location = study.getLocation();
        this.notice = study.getNotice();
        this.status = study.getStatus();
        this.createdBy = study.getCreatedBy();
        this.createdDate = study.getCreatedDate();
        this.modifiedDate = study.getModifiedDate();
        this.schedules = study.getSchedules().stream()
                .map(ScheduleDto::new)
                .collect(Collectors.toList());
        this.curriculums = study.getCurriculums().stream()
                .map(CurriculumDto::new)
                .collect(Collectors.toList());
    }

    @Getter
    public static class ScheduleDto {
        private final Integer dayOfWeek;
        private final String meetTime;

        public ScheduleDto(com.reverse.nsu.entity.StudySchedule schedule) {
            this.dayOfWeek = schedule.getDayOfWeek();
            this.meetTime = schedule.getMeetTime().toString();
        }
    }

    @Getter
    public static class CurriculumDto {
        private final Integer week;
        private final String contents;

        public CurriculumDto(com.reverse.nsu.entity.StudyCurriculum curriculum) {
            this.week = curriculum.getWeek();
            this.contents = curriculum.getContents();
        }
    }
}
