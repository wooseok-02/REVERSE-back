package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "RECRUITMENT_PAGE")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitmentPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pageId")
    private Integer id;

    @Column(name = "recruitmentId", nullable = false, unique = true)
    private Integer recruitmentId;

    @Column(name = "heroYear", columnDefinition = "CHAR(4)", nullable = false)
    private String heroYear;

    @Column(name = "heroTitle", length = 100, nullable = false)
    private String heroTitle;

    @Column(name = "heroSubTitle", length = 255)
    private String heroSubTitle;

    @Column(name = "heroBtnText", length = 30, nullable = false)
    private String heroBtnText;

    @Column(name = "heroBgUrl", columnDefinition = "TEXT")
    private String heroBgUrl;

    @Column(name = "isActive", columnDefinition = "TINYINT(1)", nullable = false)
    private Boolean isActive; // TINYINT(1) 매핑

    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;

    @Column(name = "createdDate", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @Column(name = "modifiedDate", nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime modifiedDate;
}