package com.reverse.nsu.dto;

import com.reverse.nsu.entity.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ProjectRequestDto {
    private String projectName;
    private String leaderId;
    private String leaderName;
    private String photoUrl;
    private String description;
    private String goal;
    private String location;
    private String notice;
    private ProjectStatus status; // PENDING, ACTIVE, CLOSED
    private List<ScheduleDto> schedules; // 진행 요일 및 시간대 목록

    @Getter
    @Setter
    public static class ScheduleDto {
        private Integer dayOfWeek; // 0=일 ~ 6=토
        private String meetTime;   // 포스트맨에서 "18:00" 형태로 들어오면 서비스에서 파싱
    }
}