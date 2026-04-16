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
@Table(name = "RECRUITMENT_APPLICATION")
public class RecruitmentApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applicationId")
    private Integer id;

    @Column(name = "recruitmentId", nullable = false)
    private Integer recruitmentId;

    // 서비스의 setUserName에 맞춰 변경
    @Column(name = "applicantName", length = 34, nullable = false)
    private String userName;

    // 서비스의 setUserMajor에 맞춰 변경
    @Column(name = "department", length = 50, nullable = false)
    private String userMajor;

    @Column(name = "studentNumber", length = 15, nullable = false)
    private String studentNumber;

    // 서비스의 setUserPhone에 맞춰 변경
    @Column(name = "phoneNumber", length = 20, nullable = false)
    private String userPhone;

    @Column(name = "grade", nullable = false)
    private Byte grade;

    // 서비스의 setUserEmail에 맞춰 변경
    @Column(name = "email", length = 100, nullable = false)
    private String userEmail;

    // 추가된 필드: 포트폴리오 URL
    @Column(name = "portfolioUrl", length = 255)
    private String portfolioUrl;

    @Builder.Default // 빌더 사용 시 기본값 유지
    @Column(name = "termsAgreed", columnDefinition = "TINYINT(1)", nullable = false)
    private Boolean termsAgreed = false;

    @Builder.Default // 빌더 사용 시 기본값 유지
    @Column(name = "status", length = 20, nullable = false)
    private String status = "PENDING";

    // 인터뷰 스케줄 (리스트가 서비스에서 쓰이고 있으니 추가)
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ApplicationInterviewSchedule> applicationInterviewSchedule = new ArrayList<>();

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;
}