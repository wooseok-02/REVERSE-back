package com.reverse.nsu.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "STUDY_APPLICATION_AVAILABILITY", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_STUDY_APPLICATION_AVAILABILITY", columnNames = {"studyApplicationId", "dayOfWeek"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyApplicationAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyApplicationAvailabilityId")
    private Integer studyApplicationAvailabilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyApplicationId", nullable = false)
    private StudyApplication studyApplication;

    @Column(name = "dayOfWeek", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "availableTime", nullable = false)
    private LocalTime availableTime;

    @Builder
    public StudyApplicationAvailability(StudyApplication studyApplication, Integer dayOfWeek, LocalTime availableTime) {
        this.studyApplication = studyApplication;
        this.dayOfWeek = dayOfWeek;
        this.availableTime = availableTime;
    }
}
