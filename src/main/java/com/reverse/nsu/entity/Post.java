package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

import com.reverse.nsu.dto.NoticeAdminRequestDto;

@Entity @Table(name = "POST") @Getter
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @Column(nullable = false, length = 15)
    private String userId;

    @Column(nullable = false)
    private Integer boardId;

    @Column(nullable = false, length = 50)
    private String postTitle;

    @Column(nullable = false, length = 4000)
    private String postContents;

    @Column(nullable = false)
    private Integer postCommentCount = 0;

    @Column(nullable = false)
    private Integer postLikeCount = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean isPinned = false;

    @Column(nullable = false)
    private Boolean isExternal = false; // 외부 공개 여부

    @Column(nullable = false, length = 20)
    private String postCategory = "동아리 활동"; // 동아리 활동 | 대외활동

    private LocalDateTime modifiedDate;

    // 공지사항 생성
    public static Post createNotice(NoticeAdminRequestDto dto, String userId, Integer boardId) {
        Post post = new Post();
        post.userId = userId;
        post.boardId = boardId;
        post.postTitle = dto.getTitle();
        post.postContents = dto.getContent();
        post.isPinned = dto.getIsPinned() != null ? dto.getIsPinned() : false;
        post.isExternal = dto.getIsExternal() != null ? dto.getIsExternal() : false;
        post.postCategory = dto.getCategory() != null ? dto.getCategory() : "동아리 활동";
        return post;
    }

    // 수정
    public void update(NoticeAdminRequestDto dto) {
        this.postTitle = dto.getTitle();
        this.postContents = dto.getContent();
        if (dto.getIsPinned() != null) this.isPinned = dto.getIsPinned();
        if (dto.getIsExternal() != null) this.isExternal = dto.getIsExternal();
        if (dto.getCategory() != null) this.postCategory = dto.getCategory();
        this.modifiedDate = LocalDateTime.now();
    }
}