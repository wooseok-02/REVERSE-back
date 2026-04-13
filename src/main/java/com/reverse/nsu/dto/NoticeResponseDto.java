// NoticeResponseDto.java
package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;

@Getter
public class NoticeResponseDto {
    private final Integer id;
    private final String title;
    private final String content;
    private final String createdAt;

    public static NoticeResponseDto from(Post post) {
        NoticeResponseDto dto = new NoticeResponseDto(post);
        return dto;
    }

    private NoticeResponseDto(Post post) {
        this.id = post.getPostId();
        this.title = post.getPostTitle();
        this.content = post.getPostContents();
        this.createdAt = post.getCreatedDate().toLocalDate().toString();
    }
}