package com.reverse.nsu.entity;

import com.reverse.nsu.dto.NoticeAdminRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "POST")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @Column(name = "userId", nullable = false, length = 15)
    private String userId;

    @Column(name = "boardId", nullable = false)
    private Integer boardId;

    @Column(name = "postCategory", length = 20)
    private String postCategory = "동아리 활동";

    @Column(name = "postTitle", nullable = false, length = 50)
    private String postTitle;

    @Column(name = "postContents", nullable = false, length = 4000)
    private String postContents;

    @Column(name = "postCommentCount", nullable = false)
    private Integer postCommentCount = 0;

    @Column(name = "postLikeCount", nullable = false)
    private Integer postLikeCount = 0;

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modifiedDate")
    private LocalDateTime modifiedDate;

    @Column(name = "isPinned", nullable = false)
    private Boolean isPinned = false;

    @Column(name = "isExternal", nullable = false)
    private Boolean isExternal = false;

    // --- 연관관계 설정 (삭제 에러 해결의 핵심) ---

    // 1. 좋아요 데이터 자동 삭제
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    // 2. 첨부파일(이미지) 데이터 자동 삭제
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostAttached> attachedFiles = new ArrayList<>();

    // 3. 댓글 및 대댓글 데이터 자동 삭제 (종호 님이 말한 FK 에러 해결)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // --- 가상 Getter & 편의 메서드 ---

    @Transient
    public boolean getIsModified() {
        return this.modifiedDate != null;
    }

    /**
     * [수정] attachedFiles에서 URL만 뽑아서 리스트로 반환
     * attachedUrl 필드명을 사용하도록 수정하여 빨간 줄을 해결했습니다.
     */
    @Transient
    public List<String> getImageUrlList() {
        if (this.attachedFiles == null) return new ArrayList<>();
        return this.attachedFiles.stream()
                .map(PostAttached::getAttachedUrl)
                .collect(Collectors.toList());
    }

    // --- 비즈니스 로직 ---

    public static Post createPost(NoticeAdminRequestDto dto, String userId, Integer boardId) {
        Post post = new Post();
        post.userId = userId;
        post.boardId = boardId;
        post.postCategory = (dto.getCategory() != null && !dto.getCategory().equals("전체"))
                ? dto.getCategory() : "동아리 활동";
        post.postTitle = dto.getTitle();
        post.postContents = dto.getContent();
        post.isPinned = (dto.getIsPinned() != null) ? dto.getIsPinned() : false;
        post.isExternal = (dto.getIsExternal() != null) ? dto.getIsExternal() : false;
        post.postCommentCount = 0;
        post.postLikeCount = 0;
        post.createdDate = LocalDateTime.now();
        return post;
    }

    public static Post createNotice(NoticeAdminRequestDto dto, String userId, Integer boardId) {
        return createPost(dto, userId, boardId);
    }

    public void incrementCommentCount() { this.postCommentCount++; }
    public void decrementCommentCount() { if (this.postCommentCount > 0) this.postCommentCount--; }
    public void incrementLikeCount() { this.postLikeCount++; }
    public void decrementLikeCount() { if (this.postLikeCount > 0) this.postLikeCount--; }

    public void update(NoticeAdminRequestDto dto) {
        this.postTitle = dto.getTitle();
        this.postContents = dto.getContent();
        if (dto.getCategory() != null && !dto.getCategory().equals("전체")) {
            this.postCategory = dto.getCategory();
        }
        if (dto.getIsPinned() != null) this.isPinned = dto.getIsPinned();
        if (dto.getIsExternal() != null) this.isExternal = dto.getIsExternal();
        this.modifiedDate = LocalDateTime.now();
    }
}