package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "APPLICATION_INTERVIEW_SCHEDULE")
public class ApplicationInterviewSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interviewScheduleId") // 스키마의 PK명과 일치
    private Integer id;

    // 지원서와 연결 (FK: applicationId)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationId", nullable = false)
    private RecruitmentApplication application;

    // 면접 슬롯 ID (FK: slotId)
    // 서비스에서 setSlotId(slotId)를 호출하므로 필드명을 slotId로 설정
    @Column(name = "slotId", nullable = false)
    private Integer slotId;
}