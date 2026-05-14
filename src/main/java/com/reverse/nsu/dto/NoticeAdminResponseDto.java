package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class NoticeAdminResponseDto {
    // 1. noticeId에서 postId로 이름을 변경합니다.
    private Integer postId;
    private String title;
    private String content;
    private Boolean isPinned;
    private Boolean isExternal;
    private String category;
    private List<String> imageUrls;

    // 2. 생성자 내부에서도 postId로 매핑되도록 수정합니다.
    public NoticeAdminResponseDto(Post post, List<String> imageUrls) {
        this.postId = post.getPostId();
        this.title = post.getPostTitle();
        this.content = post.getPostContents();
        this.isPinned = post.getIsPinned();
        this.isExternal = post.getIsExternal();
        this.category = post.getPostCategory();
        this.imageUrls = imageUrls;
    }

    // 3. 임시 생성자도 postId로 이름을 맞춰줍니다.
    public NoticeAdminResponseDto(Integer postId) {
        this.postId = postId;
    }
}