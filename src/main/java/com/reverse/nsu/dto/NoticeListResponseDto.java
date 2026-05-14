package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeListResponseDto {
    private Integer postId;
    private String title;
    private String userId;
    private String createdAt;
    private String category;
    private Boolean isExternal;
    private boolean isModified;
    private Integer commentCount;
    private Integer likeCount;

    public NoticeListResponseDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getPostTitle();
        this.userId = post.getUserId();


        this.createdAt = post.getCreatedDate().toLocalDate().toString();

        this.category = post.getPostCategory();
        this.isModified = post.getIsModified();
        this.isExternal = post.getIsExternal();
        this.commentCount = post.getPostCommentCount();
        this.likeCount = post.getPostLikeCount();
    }
}