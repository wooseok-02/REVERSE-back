package com.reverse.nsu.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "STUDY_SCHEDULE", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_STUDY_SCHEDULE", columnNames = {"studyId", "dayOfWeek"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyScheduleId")
    private Integer studyScheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyId", nullable = false)
    private Study study;

    @Column(name = "dayOfWeek", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "meetTime", nullable = false)
    private LocalTime meetTime;

    @Builder
    public StudySchedule(Study study, Integer dayOfWeek, LocalTime meetTime) {
        this.study = study;
        this.dayOfWeek = dayOfWeek;
        this.meetTime = meetTime;
    }
}
