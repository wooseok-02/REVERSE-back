package com.reverse.nsu.entity;

import com.reverse.nsu.dto.ClubProjectRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "CLUB_PROJECT")
@Getter
@NoArgsConstructor
public class ClubProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer projectId;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false, length = 100)
    private String projectName;

    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(length = 500)
    private String projectUrl;

    @Column(nullable = false, length = 15)
    private String updatedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    // DTO로 객체 생성
    public static ClubProject from(ClubProjectRequestDto dto) {
        ClubProject entity = new ClubProject();
        entity.projectName = dto.getProjectName();
        entity.thumbnailUrl = dto.getThumbnailUrl();
        entity.projectUrl = dto.getProjectUrl();
        entity.sortOrder = dto.getSortOrder() != null ? dto.getSortOrder() : 0;
        entity.updatedBy = dto.getUpdatedBy();
        return entity;
    }
}