package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SCHEDULE_CATEGORY")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScheduleCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId")
    private Integer id;

    @Column(name = "categoryName", length = 30, nullable = false)
    private String categoryName;

    @Column(name = "colorCode", columnDefinition = "CHAR(7)", nullable = false)
    private String colorCode;

    @Column(name = "sortOrder", nullable = false)
    private Integer sortOrder;

    @Column(name = "isVisible", nullable = false)
    private Boolean isVisible;

    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;

    public void update(String categoryName, String colorCode, Integer sortOrder, Boolean isVisible, String updatedBy) {
        this.categoryName = categoryName;
        this.colorCode = colorCode;
        this.sortOrder = sortOrder;
        this.isVisible = isVisible;
        this.updatedBy = updatedBy;
    }
}
