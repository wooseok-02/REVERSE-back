package com.reverse.nsu.dto;

import lombok.*;

@Getter
@AllArgsConstructor @NoArgsConstructor
public class ClubProjectRequestDto {

    private String projectName;     // 프로젝트 이름
    private String thumbnailUrl;    // R2에서 받은 이미지 URL
    private String projectUrl;      // 프로젝트 외부 링크
    private Integer sortOrder;      // 노출 순서
    private String updatedBy;       // 수정 관리자 ID
}