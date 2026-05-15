package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

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
    // [추가] 목록에서도 이미지를 보여주기 위한 필드
    private List<String> imageUrls;

    public NoticeListResponseDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getPostTitle();
        this.userId = post.getUserId();

        // 작성일 포맷팅
        this.createdAt = post.getCreatedDate().toLocalDate().toString();

        this.category = post.getPostCategory();
        this.isModified = post.getIsModified();
        this.isExternal = post.getIsExternal();
        this.commentCount = post.getPostCommentCount();
        this.likeCount = post.getPostLikeCount();

        // [추가] Post 엔티티에 만들어둔 getImageUrlList() 메서드를 사용하여
        // 첨부된 이미지 URL들을 리스트로 담습니다.
        this.imageUrls = post.getImageUrlList();
    }
}