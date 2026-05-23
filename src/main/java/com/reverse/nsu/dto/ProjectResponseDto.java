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
    private final String photoUrl; // 💡 Cloudflare R2 이미지 주소가 매핑되는 곳
    private final String description;
    private final String goal;
    private final Integer memberCount;
    private final String location;
    private final String notice;
    private final ProjectStatus status;
    private final List<ScheduleResponseDto> schedules; // 진행 요일 및 시간대 목록

    public ProjectResponseDto(Project project) {
        this.projectId = project.getProjectId();
        this.projectName = project.getProjectName();
        this.leaderId = project.getLeaderId();
        this.leaderName = project.getLeaderName();
        this.photoUrl = project.getPhotoUrl(); // 💡 엔티티에 저장된 R2 URL 주소를 DTO에 바인딩
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
            this.meetTime = schedule.getMeetTime().toString(); // LocalTime을 "18:00" 형태의 문자열로 포맷팅
        }
    }
}