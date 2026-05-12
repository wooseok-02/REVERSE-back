package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;

@Getter
public class NoticeListResponseDto {
    private final Integer id;
    private final String title;
    private final String createdAt;
    private final String userId;     // 작성자
    private final String category;   // 카테고리
    private final Boolean isExternal;

    public NoticeListResponseDto(Post post) {
        this.id = post.getPostId();
        this.title = post.getPostTitle();
        this.createdAt = post.getCreatedDate().toLocalDate().toString();
        this.userId = post.getUserId();
        this.category = post.getPostCategory();
        this.isExternal = post.getIsExternal();
    }
}