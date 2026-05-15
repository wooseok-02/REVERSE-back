package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
// [필수 추가] 아래 두 줄이 없어서 에러가 났던 거예요!
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COMMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false)
    private Post post;

    @Column(nullable = false, length = 15)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentCommentId")
    private Comment parent;

    // cascade 설정 덕분에 부모 삭제 시 대댓글도 자동 삭제됩니다.
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Column(nullable = false, length = 1500)
    private String commentDetail;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime modifiedDate;

    // 원댓글 작성 로직
    public static Comment create(Post post, String userId, String commentDetail) {
        Comment comment = new Comment();
        comment.post = post;
        comment.userId = userId;
        comment.commentDetail = commentDetail;
        return comment;
    }

    // 대댓글 작성 로직 (부모 Comment 객체를 직접 받음)
    public static Comment createReply(Post post, String userId, Comment parent, String commentDetail) {
        Comment comment = new Comment();
        comment.post = post;
        comment.userId = userId;
        comment.parent = parent;
        comment.commentDetail = commentDetail;
        return comment;
    }

    public void update(String commentDetail) {
        this.commentDetail = commentDetail;
    }
}