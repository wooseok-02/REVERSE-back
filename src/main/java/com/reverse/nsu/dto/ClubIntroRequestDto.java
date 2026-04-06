package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClubIntroRequestDto {

    private String title;       // 동아리 이름
    private String subTitle;    // 부제목
    private String bannerUrl;   // R2에서 받은 이미지 URL
    private Boolean isActive;   // 노출 여부
    private String updatedBy;   // 수정 관리자 ID
}