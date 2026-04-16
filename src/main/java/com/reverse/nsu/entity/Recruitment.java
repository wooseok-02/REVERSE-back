package com.reverse.nsu.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(name = "RECRUITMENT")
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitmentId")
    private Integer id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "applyStartDate", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime applyStartDate;

    @Column(name = "applyEndDate", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime applyEndDate;

    // 아까 해결했던 방식 그대로!
    @Column(name = "isActive", columnDefinition = "TINYINT(1)", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;
}