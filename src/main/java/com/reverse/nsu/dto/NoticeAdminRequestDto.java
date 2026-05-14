package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class NoticeAdminRequestDto {
    private Integer postId;
    private String title;
    private String content;
    private Boolean isPinned;
    private Boolean isExternal;
    private String category;    // DB의 postCategory 컬럼으로 들어갈 값 (예: 자유, 대외활동)
    private List<String> imageUrls;
}