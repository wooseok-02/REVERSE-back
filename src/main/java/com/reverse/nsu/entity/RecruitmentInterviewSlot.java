package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "RECRUITMENT_INTERVIEW_SLOT")
public class RecruitmentInterviewSlot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slotId")
    private Integer slotId; // id에서 slotId로 수정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitmentId", nullable = false)
    private Recruitment recruitment;

    @Column(name = "slotDate")
    private LocalDate slotDate;

    @Column(name = "interviewDate")
    @Transient
    private LocalDate interviewDate; // 스키마에 맞춰 추가

    @Column(name = "startTime")
    @Transient
    private LocalTime startTime; // 스키마에 맞춰 추가

    @Column(name = "endTime")
    @Transient
    private LocalTime endTime; // 스키마에 맞춰 추가

    @Column(name = "capacity", nullable = false)
    @Builder.Default
    private Integer capacity = 1;

    @Column(name = "maxCapacity")
    @Transient
    private Integer maxCapacity; // 스키마에 맞춰 추가

    @Column(name = "currentCount")
    @Transient
    private Integer currentCount; // 스키마에 맞춰 추가

    @Column(name = "isActive", columnDefinition = "TINYINT(1)", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "updatedBy")
    @Transient
    private String updatedBy;

    // 서비스 코드 호환용 브릿지 메서드 (필요시 유지)
    public LocalDate getInterviewDate() {
        return this.interviewDate != null ? this.interviewDate : this.slotDate;
    }
}