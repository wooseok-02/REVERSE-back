package com.reverse.nsu.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "PROJECT_SCHEDULE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "projectScheduleId")
    private Integer projectScheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;

    @Column(name = "dayOfWeek", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "meetTime", nullable = false)
    private LocalTime meetTime;

    @Builder
    public ProjectSchedule(Project project, Integer dayOfWeek, LocalTime meetTime) {
        this.project = project;
        this.dayOfWeek = dayOfWeek;
        this.meetTime = meetTime;
    }
}