package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "APPLICATION_APPLY_FIELD")
public class ApplicationApplyField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applyFieldId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationId")
    private RecruitmentApplication application;

    @Column(nullable = false)
    private String applyField; // 예: "메인프로젝트", "토이프로젝트", "스터디"
}