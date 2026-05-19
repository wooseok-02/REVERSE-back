package com.reverse.nsu.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "STUDY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyId")
    private Integer studyId;

    @Column(name = "leaderId", nullable = false, length = 15)
    private String leaderId;

    @Column(name = "studyName", nullable = false, length = 100)
    private String studyName;

    @Column(name = "leaderName", nullable = false, length = 34)
    private String leaderName;

    @Column(name = "language", length = 100)
    private String language;

    @Column(name = "techStack", length = 255)
    private String techStack;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "goal", length = 500)
    private String goal;

    @Column(name = "memberCount", nullable = false)
    private Integer memberCount = 0;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "notice", length = 500)
    private String notice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StudyStatus status = StudyStatus.ACTIVE;

    @Column(name = "createdBy", nullable = false, length = 15)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudySchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyCurriculum> curriculums = new ArrayList<>();

    @Builder
    public Study(String leaderId, String studyName, String leaderName, String language,
                 String techStack, String description, String goal, String location,
                 String notice, StudyStatus status, String createdBy) {
        this.leaderId = leaderId;
        this.studyName = studyName;
        this.leaderName = leaderName;
        this.language = language;
        this.techStack = techStack;
        this.description = description;
        this.goal = goal;
        this.memberCount = 0;
        this.location = location;
        this.notice = notice;
        this.status = status != null ? status : StudyStatus.ACTIVE;
        this.createdBy = createdBy;
    }

    public void update(String studyName, String leaderName, String language, String techStack,
                       String description, String goal, String location, String notice,
                       StudyStatus status) {
        if (studyName != null) this.studyName = studyName;
        if (leaderName != null) this.leaderName = leaderName;
        if (language != null) this.language = language;
        if (techStack != null) this.techStack = techStack;
        if (description != null) this.description = description;
        if (goal != null) this.goal = goal;
        if (location != null) this.location = location;
        if (notice != null) this.notice = notice;
        if (status != null) this.status = status;
    }

    public void updateMemberCount(int count) {
        this.memberCount = count;
    }
}
