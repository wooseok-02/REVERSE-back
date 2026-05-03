package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity @Table(name = "POST_ATTACHED") @Getter @NoArgsConstructor
public class PostAttached {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postAttachedId;

    @Column(nullable = false, length = 15)
    private String userId;

    @Column(nullable = false)
    private Integer postId;

    @Column(nullable = false, length = 260)
    private String attachedName;

    @Column(nullable = false)
    private String attachedUrl;

    @Column(nullable = false)
    private Integer attachedSize;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    public static PostAttached create(Integer postId, String userId, String url) {
        PostAttached pa = new PostAttached();
        pa.postId = postId;
        pa.userId = userId;
        pa.attachedUrl = url;
        pa.attachedName = url.substring(url.lastIndexOf("/") + 1); // URL에서 파일명 추출
        pa.attachedSize = 0;
        return pa;
    }
}