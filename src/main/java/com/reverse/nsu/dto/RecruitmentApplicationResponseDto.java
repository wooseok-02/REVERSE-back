package com.reverse.nsu.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentApplicationResponseDto {
    private Integer applicationId;
    private String studentName;
    private String studentId;
    private String major;
    private Byte grade;
    private String status; // PENDING, APPROVED, REJECTED 등
    private LocalDateTime createdAt;

    // --- 상세 조회를 위해 추가되는 필드들 ---
    private String userPhone;    // 연락처
    private String userEmail;    // 이메일
    private String portfolioUrl; // 포트폴리오 링크
    private Boolean termsAgreed; // 약관 동의 여부
}