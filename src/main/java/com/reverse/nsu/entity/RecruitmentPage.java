package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "RECRUITMENT_PAGE")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RecruitmentPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pageId")
    private Integer pageId; // 스키마와 명칭 통일

    // 연관관계 매핑 (선택 사항이나 권장)
    // 연관관계 없이 가고 싶다면 기존처럼 Integer recruitmentId로 두셔도 무방합니다.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitmentId", referencedColumnName = "recruitmentId", unique = true)
    private Recruitment recruitment;

    @Column(name = "heroYear", columnDefinition = "CHAR(4)", nullable = false)
    private String heroYear;

    @Column(name = "heroTitle", length = 100, nullable = false)
    private String heroTitle;

    @Column(name = "heroSubTitle", length = 255)
    private String heroSubTitle;

    @Column(name = "heroBtnText", length = 30)
    @Builder.Default
    private String heroBtnText = "신청하기";

    @Column(name = "heroBgUrl", columnDefinition = "TEXT")
    private String heroBgUrl;

    @Column(name = "isActive", columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "updatedBy", length = 15)
    private String updatedBy;

    // --- 스키마에 새로 추가된 컬럼들 ---
    @Column(name = "heroImageUrl", length = 255)
    private String heroImageUrl;

    @Column(name = "subtitle", length = 255)
    private String subtitle;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "year", length = 255)
    private String year;
    // ----------------------------

    @Column(name = "createdDate", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modifiedDate")
    private LocalDateTime modifiedDate;

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