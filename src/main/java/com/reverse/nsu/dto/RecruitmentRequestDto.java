package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Recruitment;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class RecruitmentRequestDto {
    private String title;
    private String description;
    private LocalDateTime applyStartDate;
    private LocalDateTime applyEndDate;
    private Boolean isActive;
    private String updatedBy;

    public Recruitment toEntity() {
        return Recruitment.builder()
                .title(this.title)
                .description(this.description)
                .applyStartDate(this.applyStartDate)
                .applyEndDate(this.applyEndDate)
                .isActive(true) // 기본적으로 공고는 활성화 상태로 저장
                .updatedBy(this.updatedBy)
                .build();
    }
}