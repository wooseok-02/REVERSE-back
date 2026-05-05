package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class NoticeAdminRequestDto {
    private Integer noticeId;   // 수정 시에만
    private String title;       // 필수
    private String content;     // 필수
    private Boolean isPinned;
    private Boolean isExternal; // 외부 공개 여부
    private String category;    // 동아리 활동 | 대외활동
    private List<String> imageUrls; // R2 업로드 후 받은 URL 목록
}