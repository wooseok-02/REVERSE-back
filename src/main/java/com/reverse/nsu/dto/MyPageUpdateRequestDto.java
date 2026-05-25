package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MyPageUpdateRequestDto {
    private String userIntroduce; // 한 줄 소개 수정용

    // 프로필 사진 변경 시 R2 업로드 완료 후 반환받은 메타데이터
    private String attachedName;
    private String attachedUrl;
    private Integer attachedSize;
}