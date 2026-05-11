package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMMENT")
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;

    @Column(nullable = false)
    private Integer postId;

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

    // 댓글 작성
    public static Comment create(Integer postId, String userId, String commentDetail) {
        Comment comment = new Comment();
        comment.postId = postId;
        comment.userId = userId;
        comment.commentDetail = commentDetail;
        return comment;
    }

    // 대댓글 작성
    public static Comment createReply(Integer postId, String userId, Integer parentCommentId, String commentDetail) {
        Comment comment = new Comment();
        comment.postId = postId;
        comment.userId = userId;
        comment.parentCommentId = parentCommentId;
        comment.commentDetail = commentDetail;
        return comment;
    }

    // 댓글 수정
    public void update(String commentDetail) {
        this.commentDetail = commentDetail;
    }
}
