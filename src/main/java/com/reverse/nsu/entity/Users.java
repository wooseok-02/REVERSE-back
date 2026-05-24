package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USERS")
public class Users {

    @Id
    @Column(name = "userId", nullable = false, length = 15)
    private String userId; // PK

    // ROLE 테이블과의 외래키 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;

    @Column(name = "userName", nullable = false, length = 34)
    private String userName;

    // [추가] DB 스키마의 userEmail 컬럼과 매칭되는 필드
    @Column(name = "userEmail", nullable = false, length = 100)
    private String userEmail;

    @Column(name = "userPassword", nullable = false, length = 255)
    private String userPassword;

    @Column(name = "userIntroduce", length = 100)
    private String userIntroduce;

    @Column(name = "userMbti", columnDefinition = "CHAR(4)")
    private String userMbti;

    @CreationTimestamp
    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;

    /**
     * 🔥 [마이페이지 비즈니스 메서드]
     * - 자기소개(한 줄 소개) 내용을 안전하게 변경합니다.
     */
    public void updateIntroduce(String userIntroduce) {
        this.userIntroduce = userIntroduce;
    }
}