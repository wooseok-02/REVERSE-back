package com.reverse.nsu.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "STUDY_APPLICATION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyApplicationId")
    private Integer studyApplicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyId", nullable = false)
    private Study study;

    @Column(name = "userId", nullable = false, length = 15)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "processedBy", length = 15)
    private String processedBy;

    @Column(name = "processedDate")
    private LocalDateTime processedDate;

    @CreationTimestamp
    @Column(name = "appliedDate", nullable = false, updatable = false)
    private LocalDateTime appliedDate;

    @OneToMany(mappedBy = "studyApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyApplicationAvailability> availabilities = new ArrayList<>();

    @Builder
    public StudyApplication(Study study, String userId) {
        this.study = study;
        this.userId = userId;
        this.status = ApplicationStatus.PENDING;
    }

    public void addAvailability(StudyApplicationAvailability availability) {
        this.availabilities.add(availability);
    }

    public void approve(String processedBy) {
        this.status = ApplicationStatus.APPROVED;
        this.processedBy = processedBy;
        this.processedDate = LocalDateTime.now();
    }

    public void reject(String processedBy) {
        this.status = ApplicationStatus.REJECTED;
        this.processedBy = processedBy;
        this.processedDate = LocalDateTime.now();
    }
}
