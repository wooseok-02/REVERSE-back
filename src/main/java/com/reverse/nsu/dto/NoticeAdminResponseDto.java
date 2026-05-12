package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class NoticeAdminResponseDto {
    private Integer noticeId;
    private String title;
    private String content;
    private Boolean isPinned;
    private Boolean isExternal;
    private String category;
    private List<String> imageUrls;

    // 서비스/컨트롤러에서 new NoticeAdminResponseDto(post, list)로 호출할 수 있게 함
    public NoticeAdminResponseDto(Post post, List<String> imageUrls) {
        this.noticeId = post.getPostId();
        this.title = post.getPostTitle();
        this.content = post.getPostContents();
        this.isPinned = post.getIsPinned();
        this.isExternal = post.getIsExternal();
        this.category = post.getPostCategory();
        this.imageUrls = imageUrls;
    }

    // 만약 기존에 Integer ID만 받는 생성자가 필요하다면 (임시용)
    public NoticeAdminResponseDto(Integer noticeId) {
        this.noticeId = noticeId;
    }
}