package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "POST_LIKE",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_POST_LIKE", columnNames = {"userId", "postId"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postLikeId;

    @Column(name = "userId", nullable = false, length = 15)
    private String userId;

    // [수정] Integer postId 대신 Post 객체와 연관관계를 맺습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false)
    private Post post;

    @CreationTimestamp
    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * 좋아요 생성 정적 팩토리 메서드
     */
    public static PostLike create(Post post, String userId) {
        PostLike postLike = new PostLike();
        postLike.post = post; // 객체 주입
        postLike.userId = userId;
        return postLike;
    }
}