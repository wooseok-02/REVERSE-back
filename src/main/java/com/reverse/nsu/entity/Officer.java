package com.reverse.nsu.entity;

import com.reverse.nsu.dto.OfficerRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "OFFICER")
@Getter
@NoArgsConstructor
public class Officer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer officerId;

    @Column(nullable = false, length = 34)
    private String name;

    @Column(nullable = false)
    private Integer generation;

    @Column(nullable = false, length = 50)
    private String role;

    @Column(length = 50)
    private String department;

    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String photoUrl;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private Boolean isVisible = true;

    @Column(nullable = false, length = 15)
    private String updatedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    // DTO로 객체 생성
    public static Officer from(OfficerRequestDto dto) {
        Officer entity = new Officer();
        entity.name = dto.getName();
        entity.generation = dto.getGeneration();
        entity.role = dto.getRole();
        entity.department = dto.getDepartment();
        entity.email = dto.getEmail();
        entity.photoUrl = dto.getPhotoUrl();
        entity.sortOrder = dto.getSortOrder() != null ? dto.getSortOrder() : 0;
        entity.isVisible = dto.getIsVisible() != null ? dto.getIsVisible() : true;
        entity.updatedBy = dto.getUpdatedBy();
        return entity;
    }
}