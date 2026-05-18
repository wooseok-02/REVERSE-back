package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Project;
import com.reverse.nsu.entity.ProjectStatus;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProjectResponseDto {
    private final Integer projectId;
    private final String projectName;
    private final String leaderId;
    private final String leaderName;
    private final String photoUrl;
    private final String description;
    private final String goal;
    private final Integer memberCount;
    private final String location;
    private final String notice;
    private final ProjectStatus status;
    private final List<ScheduleResponseDto> schedules;

    public ProjectResponseDto(Project project) {
        this.projectId = project.getProjectId();
        this.projectName = project.getProjectName();
        this.leaderId = project.getLeaderId();
        this.leaderName = project.getLeaderName();
        this.photoUrl = project.getPhotoUrl();
        this.description = project.getDescription();
        this.goal = project.getGoal();
        this.memberCount = project.getMemberCount();
        this.location = project.getLocation();
        this.notice = project.getNotice();
        this.status = project.getStatus();
        this.schedules = project.getSchedules().stream()
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());
    }

    @Getter
    public static class ScheduleResponseDto {
        private final Integer dayOfWeek;
        private final String meetTime;

        public ScheduleResponseDto(com.reverse.nsu.entity.ProjectSchedule schedule) {
            this.dayOfWeek = schedule.getDayOfWeek();
            this.meetTime = schedule.getMeetTime().toString(); // "18:00" 형태 포맷팅
        }
    }
}