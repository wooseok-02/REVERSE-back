package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "APPLICATION_APPLY_FIELD", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_APPLICATION_APPLY_FIELD", columnNames = {"applicationId", "applyField"})
})
public class ApplicationApplyField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applyFieldId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationId", nullable = false)
    private RecruitmentApplication application;

    @Column(name = "applyField", length = 50, nullable = false)
    private String applyField; // 예: "메인프로젝트", "토이프로젝트", "스터디"
}
