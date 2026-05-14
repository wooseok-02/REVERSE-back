package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;
import java.util.List;

@Getter
public class NoticeResponseDto {
    // 1. id를 postId로 변경하여 전체적인 명명 규칙을 통일합니다.
    private final Integer postId;
    private final String title;
    private final String content;
    private final String createdAt;
    private final String userId;
    private final String category;
    private final Boolean isExternal;
    private final List<String> imageUrls;

    public static NoticeResponseDto from(Post post, List<String> imageUrls) {
        return new NoticeResponseDto(post, imageUrls);
    }

    private NoticeResponseDto(Post post, List<String> imageUrls) {
        this.postId = post.getPostId();
        this.title = post.getPostTitle();
        this.content = post.getPostContents();
        this.createdAt = post.getCreatedDate().toLocalDate().toString();
        this.userId = post.getUserId();
        this.category = post.getPostCategory();
        this.isExternal = post.getIsExternal();
        this.imageUrls = imageUrls;
    }
}