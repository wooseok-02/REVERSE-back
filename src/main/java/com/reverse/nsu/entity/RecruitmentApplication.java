package com.reverse.nsu.entity;

import lombok.*;
import jakarta.persistence.*;
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
public class RecruitmentApplication extends BaseTimeEntity { // BaseTimeEntity 상속 유지

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applicationId")
    private Integer applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitmentId", nullable = false)
    private Recruitment recruitment;

    // Java 필드명과 DB 컬럼명을 일치시켜서 서비스 코드 에러 방지
    @Column(name = "applicantName", length = 34, nullable = false)
    private String applicantName;

    @Column(name = "department", length = 50, nullable = false)
    private String department;

    @Column(name = "studentNumber", length = 15, nullable = false)
    private String studentNumber;

    @Column(name = "phoneNumber", length = 20, nullable = false)
    private String phoneNumber;

    @Column(name = "grade", nullable = false)
    private Byte grade;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "portfolioUrl", length = 500)
    private String portfolioUrl;

    @Builder.Default
    @Column(name = "termsAgreed", columnDefinition = "TINYINT(1)", nullable = false)
    private Integer termsAgreed = 0;

    @Builder.Default
    @Column(name = "status", length = 20, nullable = false)
    private String status = "PENDING";

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApplicationInterviewSchedule> applicationInterviewSchedule = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApplicationApplyField> applyFields = new ArrayList<>();

    public void addApplyField(String applyField) {
        applyFields.add(ApplicationApplyField.builder()
                .application(this)
                .applyField(applyField)
                .build());
    }

    public void addInterviewSchedule(RecruitmentInterviewSlot slot) {
        applicationInterviewSchedule.add(ApplicationInterviewSchedule.builder()
                .application(this)
                .interviewSlot(slot)
                .build());
    }

    // 날짜 필드는 BaseTimeEntity에서 상속받으므로 여기서 삭제함!
}
