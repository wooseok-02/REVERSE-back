// NoticeListResponseDto.java
package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;

@Getter
public class NoticeListResponseDto {
    private final Integer id;
    private final String title;
    private final String createdAt;

    public NoticeListResponseDto(Post post) {
        this.id = post.getPostId();
        this.title = post.getPostTitle();
        this.createdAt = post.getCreatedDate().toLocalDate().toString();
    }
}