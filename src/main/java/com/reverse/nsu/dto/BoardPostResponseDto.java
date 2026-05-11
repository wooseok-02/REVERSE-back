package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;
import java.util.List;

@Getter
public class BoardPostResponseDto {
    private final Integer id;
    private final String title;
    private final String content;
    private final String userId;
    private final String createdAt;
    private final String modifiedAt;
    private final Integer commentCount;
    private final Integer likeCount;
    private final List<String> imageUrls;

    public static BoardPostResponseDto from(Post post, List<String> imageUrls) {
        return new BoardPostResponseDto(post, imageUrls);
    }

    private BoardPostResponseDto(Post post, List<String> imageUrls) {
        this.id = post.getPostId();
        this.title = post.getPostTitle();
        this.content = post.getPostContents();
        this.userId = post.getUserId();
        this.createdAt = post.getCreatedDate().toLocalDate().toString();
        this.modifiedAt = post.getModifiedDate() != null
                ? post.getModifiedDate().toLocalDate().toString() : null;
        this.commentCount = post.getPostCommentCount();
        this.likeCount = post.getPostLikeCount();
        this.imageUrls = imageUrls;
    }
}
