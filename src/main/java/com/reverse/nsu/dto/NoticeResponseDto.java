package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;
import java.util.List;

@Getter
public class NoticeResponseDto {
    private final Integer id;
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
        this.id = post.getPostId();
        this.title = post.getPostTitle();
        this.content = post.getPostContents();
        this.createdAt = post.getCreatedDate().toLocalDate().toString();
        this.userId = post.getUserId();
        this.category = post.getPostCategory(); // 이제 가상 필드가 아닌 실제 필드 호출!
        this.isExternal = post.getIsExternal();
        this.imageUrls = imageUrls;
    }
}