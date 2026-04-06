package com.reverse.nsu.entity;

import com.reverse.nsu.dto.ClubIntroRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "CLUB_INTRO")
@Getter
@NoArgsConstructor
public class ClubIntro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer clubIntroId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String subTitle;

    @Column(columnDefinition = "TEXT")
    private String bannerUrl;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, length = 15)
    private String updatedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    // DTO로 객체 생성
    public static ClubIntro from(ClubIntroRequestDto dto) {
        ClubIntro entity = new ClubIntro();
        entity.title = dto.getTitle();
        entity.subTitle = dto.getSubTitle();
        entity.bannerUrl = dto.getBannerUrl();
        entity.isActive = dto.getIsActive() != null ? dto.getIsActive() : true;
        entity.updatedBy = dto.getUpdatedBy();
        return entity;
    }
}