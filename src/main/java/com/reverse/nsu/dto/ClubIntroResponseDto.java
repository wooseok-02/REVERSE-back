package com.reverse.nsu.dto;

import com.reverse.nsu.entity.ClubIntro;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ClubIntroResponseDto {
    private Integer clubIntroId;
    private String title;
    private String subTitle;
    private String bannerUrl;
    private Boolean isActive;
    private String updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    // Entity -> DTO 변환 생성자
    public ClubIntroResponseDto(ClubIntro entity) {
        this.clubIntroId = entity.getClubIntroId();
        this.title = entity.getTitle();
        this.subTitle = entity.getSubTitle();
        this.bannerUrl = entity.getBannerUrl();
        this.isActive = entity.getIsActive();
        this.updatedBy = entity.getUpdatedBy();
        this.createdDate = entity.getCreatedDate();
        this.modifiedDate = entity.getModifiedDate();
    }
}