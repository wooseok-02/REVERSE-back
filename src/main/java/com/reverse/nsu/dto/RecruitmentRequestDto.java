package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Recruitment;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitmentRequestDto {

    // --- 기존 공고 기본 정보 ---
    private String title;
    private String description;
    private LocalDateTime applyStartDate;
    private LocalDateTime applyEndDate;
    private Boolean isActive;
    private String updatedBy;
    private Integer recruitmentId;

    // --- 추가: 상세 페이지(Hero 섹션)용 정보 ---
    private String heroYear;
    private String heroTitle;
    private String heroSubTitle;
    private String heroBgUrl;
    private String heroBtnText; // <-- 이 친구가 있어야 null 에러가 안 납니다!

    // --- 신청자 관련 정보 ---
    private String userName;
    private List<Integer> selectedSlotIds;
    private String userMajor;
    private String userPhone;
    private String userEmail;
    private String portfolioUrl;

    public Recruitment toEntity() {
        return Recruitment.builder()
                .title(this.title)
                .description(this.description)
                .applyStartDate(this.applyStartDate)
                .applyEndDate(this.applyEndDate)
                .isActive(this.isActive != null ? this.isActive : true)
                .updatedBy(this.updatedBy)
                .build();
    }
}