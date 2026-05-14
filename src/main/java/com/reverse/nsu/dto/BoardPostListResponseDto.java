package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;

@Getter
public class BoardPostListResponseDto {
    // 1. id를 postId로 변경하여 전체 프로젝트의 명명 규칙을 통일합니다.
    private final Integer postId;
    private final String title;
    private final String userId;
    private final String createdAt;
    private final Integer commentCount;
    private final Integer likeCount;

    public BoardPostListResponseDto(Post post) {
        // 2. 매핑되는 필드명도 postId로 수정합니다.
        this.postId = post.getPostId();
        this.title = post.getPostTitle();
        this.userId = post.getUserId();
        this.createdAt = post.getCreatedDate().toLocalDate().toString();
        this.commentCount = post.getPostCommentCount();
        this.likeCount = post.getPostLikeCount();
    }
}