package com.reverse.nsu.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "RECRUITMENT_APPLICATION", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_APPLICATION_RECRUITMENT_STUDENT", columnNames = {"recruitmentId", "studentNumber"})
})
public class RecruitmentApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applicationId") // id -> applicationId로 수정
    private Integer applicationId;

    // Integer recruitmentId -> Recruitment 객체 참조로 수정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitmentId", nullable = false)
    private Recruitment recruitment;

    @Column(name = "applicantName", length = 34, nullable = false)
    private String userName;

    @Column(name = "department", length = 50, nullable = false)
    private String userMajor;

    @Column(name = "studentNumber", length = 15, nullable = false)
    private String studentNumber;

    @Column(name = "phoneNumber", length = 20, nullable = false)
    private String userPhone;

    @Column(name = "grade", nullable = false)
    private Byte grade;

    @Column(name = "email", length = 100, nullable = false)
    private String userEmail;

    @Column(name = "portfolioUrl", length = 255)
    private String portfolioUrl;

    @Builder.Default
    @Column(name = "termsAgreed", columnDefinition = "TINYINT(1)", nullable = false)
    private Boolean termsAgreed = false;

    @Builder.Default
    @Column(name = "status", length = 20, nullable = false)
    private String status = "PENDING";

    // 인터뷰 스케줄 (기존 로직 유지)
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ApplicationInterviewSchedule> applicationInterviewSchedule = new ArrayList<>();

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modifiedDate", nullable = false)
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