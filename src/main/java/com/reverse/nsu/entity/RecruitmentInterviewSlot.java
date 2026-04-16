package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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
    private Integer id; // 자바 내부에서는 id로 사용

    @Column(name = "recruitmentId", nullable = false)
    private Integer recruitmentId;

    // 서비스 코드가 s.getInterviewDate()를 찾고 있으므로 추가
    @Column(name = "slotDate", nullable = false)
    private LocalDate slotDate;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "isActive", nullable = false)
    private Boolean isActive;

    // --- 서비스 코드와의 호환성을 위한 브릿지 메서드 ---
    public Integer getSlotId() { return this.id; }
    public LocalDate getInterviewDate() { return this.slotDate; }
}