package com.reverse.nsu.entity;

import jakarta.persistence.*;
<<<<<<< HEAD
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
=======
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "POST_LIKE")
@Getter
>>>>>>> 3fee7c5510531ab65f364f31094a78799a48622e
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postLikeId;

<<<<<<< HEAD
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
=======
    @Column(nullable = false, length = 15)
    private String userId;

    @Column(nullable = false)
    private Integer postId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    public static PostLike create(String userId, Integer postId) {
        PostLike postLike = new PostLike();
        postLike.userId = userId;
        postLike.postId = postId;
        return postLike;
    }
}
>>>>>>> 3fee7c5510531ab65f364f31094a78799a48622e
