package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "SCHEDULE")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Schedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduleId")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    private ScheduleCategory category;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "startTime")
    private LocalTime startTime;

    @Column(name = "endTime")
    private LocalTime endTime;

    @Column(name = "isAllDay", nullable = false)
    private Boolean isAllDay;

    @Column(name = "isVisible", nullable = false)
    private Boolean isVisible;

    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;

    public void update(ScheduleCategory category, String title, String description,
                       LocalDate startDate, LocalDate endDate,
                       LocalTime startTime, LocalTime endTime,
                       Boolean isAllDay, Boolean isVisible, String updatedBy) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAllDay = isAllDay;
        this.isVisible = isVisible;
        this.updatedBy = updatedBy;
    }
}
