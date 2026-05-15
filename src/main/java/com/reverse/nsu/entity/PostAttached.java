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

    // [수정] 단순 ID가 아니라 Post 객체와 연관관계를 맺어야 합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false)
    private Post post;

    @Column(name = "attachedName", nullable = false, length = 260)
    private String attachedName;

    @Column(name = "attachedUrl", nullable = false, columnDefinition = "TEXT")
    private String attachedUrl;

    @Column(name = "attachedSize", nullable = false)
    private Integer attachedSize;

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modifiedDate")
    private LocalDateTime modifiedDate = LocalDateTime.now();

    /**
     * 첨부파일 생성 정적 팩토리 메서드
     */
    public static PostAttached create(Post post, String userId, String url) {
        PostAttached pa = new PostAttached();
        pa.post = post; // 객체 주입
        pa.userId = userId;
        pa.attachedUrl = url;
        // URL에서 파일명 추출 로직 (안정성을 위해 null 체크 추가 권장)
        pa.attachedName = url != null && url.contains("/") ? url.substring(url.lastIndexOf("/") + 1) : "unknown";
        pa.attachedSize = 0;
        return pa;
    }
}