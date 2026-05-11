package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeListResponseDto {
    private Integer id;
    private String title;
    private String userId;
    private String createdAt;
    private String category;       // 실제 DB의 postCategory 값
    private Boolean isExternal;
    private boolean isModified;
    private Integer commentCount;
    private Integer likeCount;

    public NoticeListResponseDto(Post post) {
        this.id = post.getPostId();
        this.title = post.getPostTitle();
        this.userId = post.getUserId();

        // 날짜 포맷팅
        this.createdAt = post.getCreatedDate().toLocalDate().toString();

        // [수정] 엔티티의 실제 필드값(postCategory)을 가져옵니다.
        this.category = post.getPostCategory();

        this.isModified = post.getIsModified();
        this.isExternal = post.getIsExternal();
        this.commentCount = post.getPostCommentCount();
        this.likeCount = post.getPostLikeCount();
    }
}