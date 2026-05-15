package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자 보안 설정
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;

    // [핵심 수정] 단순 Integer가 아니라 Post 객체와 연관관계를 맺어야 합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false)
    private Post post;

    @Column(nullable = false, length = 15)
    private String userId;

    @Column(name = "parentCommentId")
    private Integer parentCommentId; // null = 원댓글, 값 있음 = 대댓글

    @Column(nullable = false, length = 1500)
    private String commentDetail;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime modifiedDate;

    // [수정] 비즈니스 로직 - Integer postId 대신 Post 객체를 받도록 변경
    public static Comment create(Post post, String userId, String commentDetail) {
        Comment comment = new Comment();
        comment.post = post;
        comment.userId = userId;
        comment.commentDetail = commentDetail;
        return comment;
    }

    // [수정] 대댓글 작성 로직
    public static Comment createReply(Post post, String userId, Integer parentCommentId, String commentDetail) {
        Comment comment = new Comment();
        comment.post = post;
        comment.userId = userId;
        comment.parentCommentId = parentCommentId;
        comment.commentDetail = commentDetail;
        return comment;
    }

    public void update(String commentDetail) {
        this.commentDetail = commentDetail;
    }
}