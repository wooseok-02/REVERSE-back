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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false)
    private Post post;

    @Column(name = "attachedName", nullable = false, length = 260)
    private String attachedName;

    // [확인] 필드명을 attachedUrl로 유지하겠습니다.
    @Column(name = "attachedUrl", nullable = false, columnDefinition = "TEXT")
    private String attachedUrl;

    @Column(name = "attachedSize", nullable = false)
    private Integer attachedSize;

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modifiedDate")
    private LocalDateTime modifiedDate; // 생성 시점에 자동 할당되므로 초기화 제거 가능

    /**
     * 첨부파일 생성 정적 팩토리 메서드
     */
    public static PostAttached create(Post post, String userId, String url) {
        PostAttached pa = new PostAttached();
        pa.post = post;
        pa.userId = userId;
        pa.attachedUrl = url;
        // 파일명 추출 로직 보완 (쿼리 파라미터 등이 붙을 경우 대비)
        pa.attachedName = extractFileName(url);
        pa.attachedSize = 0;
        pa.createdDate = LocalDateTime.now();
        return pa;
    }

    private static String extractFileName(String url) {
        if (url == null || url.isEmpty()) return "unknown";
        try {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            // URL에 ? 파라미터가 붙어있을 경우 제거 (S3 URL 등 대비)
            return fileName.contains("?") ? fileName.substring(0, fileName.indexOf("?")) : fileName;
        } catch (Exception e) {
            return "unknown";
        }
    }
}