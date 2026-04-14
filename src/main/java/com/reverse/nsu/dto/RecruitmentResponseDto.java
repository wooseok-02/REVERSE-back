package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Recruitment;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentResponseDto {
    private Integer id;
    private String title;
    private String description;
    private Boolean isActive; // 추가: 노출 여부
    private String createdAt;
    private String updatedAt;

    public static RecruitmentResponseDto from(Recruitment recruitment) {
        return RecruitmentResponseDto.builder()
                .id(recruitment.getId())
                .title(recruitment.getTitle())
                .description(recruitment.getDescription())
                .isActive(recruitment.getIsActive())
                .createdAt(recruitment.getCreatedAt() != null ? String.valueOf(recruitment.getCreatedAt()) : "")
                .updatedAt(recruitment.getUpdatedAt() != null ? String.valueOf(recruitment.getUpdatedAt()) : "")
                .build();
    }
}