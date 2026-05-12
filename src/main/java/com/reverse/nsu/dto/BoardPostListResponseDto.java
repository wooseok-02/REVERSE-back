package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;

@Getter
public class BoardPostListResponseDto {
    private final Integer id;
    private final String title;
    private final String userId;
    private final String createdAt;
    private final Integer commentCount;
    private final Integer likeCount;

    public BoardPostListResponseDto(Post post) {
        this.id = post.getPostId();
        this.title = post.getPostTitle();
        this.userId = post.getUserId();
        this.createdAt = post.getCreatedDate().toLocalDate().toString();
        this.commentCount = post.getPostCommentCount();
        this.likeCount = post.getPostLikeCount();
    }
}
