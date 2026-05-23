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
@Table(name = "PROJECT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "projectId")
    private Integer projectId;

    @Column(name = "leaderId", nullable = false, length = 15)
    private String leaderId;

    @Column(name = "projectName", nullable = false, length = 100)
    private String projectName;

    @Column(name = "leaderName", nullable = false, length = 34)
    private String leaderName;

    @Column(name = "photoUrl", columnDefinition = "TEXT")
    private String photoUrl;

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
    private ProjectStatus status = ProjectStatus.ACTIVE;

    @Column(name = "createdBy", nullable = false, length = 15)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;

    // 프로젝트 삭제 시 연관된 스케줄들도 Cascade 삭제 (Cascade 옵션 아주 좋습니다!)
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSchedule> schedules = new ArrayList<>();

    // 프로젝트 삭제 시 소속된 멤버 매핑 데이터들도 Cascade 삭제
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> members = new ArrayList<>();

    @Builder
    public Project(String leaderId, String projectName, String leaderName, String photoUrl,
                   String description, String goal, Integer memberCount, String location,
                   String notice, ProjectStatus status, String createdBy) {
        this.leaderId = leaderId;
        this.projectName = projectName;
        this.leaderName = leaderName;
        this.photoUrl = photoUrl;
        this.description = description;
        this.goal = goal;
        this.memberCount = memberCount != null ? memberCount : 0;
        this.location = location;
        this.notice = notice;
        this.status = status != null ? status : ProjectStatus.ACTIVE;
        this.createdBy = createdBy;
    }

    /**
     * 🔥 [수정 비즈니스 메서드 추가]
     * - 외부 DTO의 변경사항을 엔티티 객체 내부에 안전하게 바인딩합니다.
     * - Null 체크를 수행하여 포스트맨이나 프론트에서 보내지 않은 값은 기존 데이터를 유지하도록 방어합니다.
     */
    public void updateProjectDetails(com.reverse.nsu.dto.ProjectRequestDto dto) {
        if (dto.getProjectName() != null && !dto.getProjectName().trim().isEmpty()) {
            this.projectName = dto.getProjectName();
        }
        if (dto.getDescription() != null) {
            this.description = dto.getDescription();
        }
        if (dto.getGoal() != null) {
            this.goal = dto.getGoal();
        }
        if (dto.getLocation() != null) {
            this.location = dto.getLocation();
        }
        if (dto.getNotice() != null) {
            this.notice = dto.getNotice();
        }
        if (dto.getStatus() != null) {
            this.status = dto.getStatus();
        }
        if (dto.getPhotoUrl() != null) {
            this.photoUrl = dto.getPhotoUrl();
        }
    }

    public void updateStatus(ProjectStatus newStatus) {
        this.status = newStatus;
    }

    public void updateMemberCount(Integer count) {
        this.memberCount = count;
    }
}