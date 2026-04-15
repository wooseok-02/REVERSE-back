package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "RECRUITMENT_APPLICATION")
public class RecruitmentApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applicationId;

    private Integer recruitmentId; // 어느 공고에 지원했는지
    private String applicantName;
    private String department;
    private String studentNumber;
    private String phoneNumber;
    private Byte grade;
    private String email;
    private String interviewMemo;
    private Boolean termsAgreed; // TINYINT(1) 매핑
    private String status = "PENDING";

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    private List<ApplicationApplyField> applyFields = new ArrayList<>();

    public void addApplyField(ApplicationApplyField field) {
        applyFields.add(field);
        field.setApplication(this);}

}