package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "POST_LIKE")
@Getter
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postLikeId;

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
