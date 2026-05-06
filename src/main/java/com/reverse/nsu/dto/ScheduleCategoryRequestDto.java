package com.reverse.nsu.dto;

import com.reverse.nsu.entity.ScheduleCategory;
import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ScheduleCategoryRequestDto {
    private String categoryName;
    private String colorCode;
    private Integer sortOrder;
    private Boolean isVisible;
    private String updatedBy;

    public ScheduleCategory toEntity() {
        return ScheduleCategory.builder()
                .categoryName(this.categoryName)
                .colorCode(this.colorCode != null ? this.colorCode : "#FFFFFF")
                .sortOrder(this.sortOrder != null ? this.sortOrder : 0)
                .isVisible(this.isVisible != null ? this.isVisible : true)
                .updatedBy(this.updatedBy)
                .build();
    }
}