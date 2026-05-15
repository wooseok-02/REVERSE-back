package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;
import java.util.List;

@Getter
public class BoardPostListResponseDto {
    private final Integer postId;
    private final String title;
    private final String userId;
    private final String createdAt;
    private final Integer commentCount;
    private final Integer likeCount;

    // [추가] 목록에서도 이미지 URL 리스트를 확인할 수 있도록 필드 추가
    private final List<String> imageUrls;

    public BoardPostListResponseDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getPostTitle();
        this.userId = post.getUserId();
        this.createdAt = post.getCreatedDate().toLocalDate().toString();
        this.commentCount = post.getPostCommentCount();
        this.likeCount = post.getPostLikeCount();

        // [추가] 엔티티에서 이미지 URL 리스트를 가져와 매핑
        // Post.java에 getImageUrlList()가 구현되어 있어야 합니다.
        this.imageUrls = post.getImageUrlList();
    }
}