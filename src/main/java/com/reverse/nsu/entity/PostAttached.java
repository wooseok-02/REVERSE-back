package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "POST_ATTACHED")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostAttached {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postAttachedId;

    @Column(name = "userId", nullable = false, length = 15)
    private String userId;

    @Column(name = "postId", nullable = false)
    private Integer postId;

    @Column(name = "attachedName", nullable = false, length = 260)
    private String attachedName;

    @Column(name = "attachedUrl", nullable = false, columnDefinition = "TEXT")
    private String attachedUrl;

    @Column(name = "attachedSize", nullable = false)
    private Integer attachedSize;

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    // [추가] 스키마 이미지에 존재하는 modifiedDate 반영
    @Column(name = "modifiedDate")
    private LocalDateTime modifiedDate = LocalDateTime.now();

    /**
     * 첨부파일 생성 정적 팩토리 메서드
     */
    public static PostAttached create(Integer postId, String userId, String url) {
        PostAttached pa = new PostAttached();
        pa.postId = postId;
        pa.userId = userId;
        pa.attachedUrl = url;
        pa.attachedName = url.substring(url.lastIndexOf("/") + 1);
        pa.attachedSize = 0;
        return pa;
    }
}