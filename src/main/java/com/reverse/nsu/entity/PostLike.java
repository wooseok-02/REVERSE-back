package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(name = "postId", nullable = false)
    private Integer postId;

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    /**
     * 좋아요 생성 정적 팩토리 메서드
     */
    public static PostLike create(Integer postId, String userId) {
        PostLike postLike = new PostLike();
        postLike.postId = postId;
        postLike.userId = userId;
        return postLike;
    }
}