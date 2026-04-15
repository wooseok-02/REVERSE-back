package com.reverse.nsu.dto;

import com.reverse.nsu.entity.ScheduleCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScheduleCategoryResponseDto {
    private Integer id;
    private String categoryName;
    private String colorCode;
    private Integer sortOrder;
    private Boolean isVisible;
    private String updatedBy;
    private String createdAt;
    private String updatedAt;

    public static ScheduleCategoryResponseDto from(ScheduleCategory category) {
        return ScheduleCategoryResponseDto.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .colorCode(category.getColorCode())
                .sortOrder(category.getSortOrder())
                .isVisible(category.getIsVisible())
                .updatedBy(category.getUpdatedBy())
                .createdAt(category.getCreatedAt() != null ? category.getCreatedAt().toString() : "")
                .updatedAt(category.getUpdatedAt() != null ? category.getUpdatedAt().toString() : "")
                .build();
    }
}
