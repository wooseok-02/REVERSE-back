package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "USER_PHOTO")
public class UserPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userAttachedId")
    private Integer userAttachedId; // PK: DB 내부 Auto Increment 세팅 연동

    @Column(name = "userId", nullable = false, length = 15)
    private String userId; // 비즈니스 식별자 및 유저 매핑용

    // 💡 스키마의 FK_USER_PHOTO_USERS 제약 조건과 1:1 관계 정석 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    private Users user;

    @Column(name = "attachedName", nullable = false, length = 260)
    private String attachedName; // 스키마의 varchar(260) 반영

    @Column(name = "attachedUrl", nullable = false, columnDefinition = "TEXT")
    private String attachedUrl; // 스키ma의 text 타입 반영 (R2 Public URL 저장소)

    @Column(name = "attachedSize", nullable = false)
    private Integer attachedSize; // 스키마의 int 타입 반영

    @CreationTimestamp
    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;

    /**
     * 💡 [비즈니스 메서드] 프로필 사진 정보 갱신(수정)용
     */
    public void updatePhotoDetails(String attachedName, String attachedUrl, Integer attachedSize) {
        this.attachedName = attachedName;
        this.attachedUrl = attachedUrl;
        this.attachedSize = attachedSize;
    }
}