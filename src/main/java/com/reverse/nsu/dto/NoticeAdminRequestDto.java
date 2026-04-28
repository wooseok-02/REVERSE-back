package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeAdminRequestDto {
    private Integer noticeId;   // 수정 시에만
    private String title;       // 필수
    private String content;     // 필수
    private Boolean isPinned;
}