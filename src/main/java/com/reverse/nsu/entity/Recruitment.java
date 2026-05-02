package com.reverse.nsu.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(name = "RECRUITMENT")
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitmentId")
    private Integer recruitmentId;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "applyStartDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyStartDate;

    @Column(name = "applyEndDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyEndDate;

    @Column(name = "isActive", columnDefinition = "TINYINT(1)", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;

    @Column(name = "createdDate", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;

    // --- 연관 관계 매핑 (조회 편의성을 위해 추가) ---

    // 1:1 관계 - 모집 상세 페이지
    @OneToOne(mappedBy = "recruitment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RecruitmentPage recruitmentPage;

    // 1:N 관계 - 지원서 목록
    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecruitmentApplication> applications = new ArrayList<>();

    // 1:N 관계 - 면접 슬롯 목록
    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecruitmentInterviewSlot> interviewSlots = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }
}