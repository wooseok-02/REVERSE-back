package com.reverse.nsu.entity;

import jakarta.persistence.*;
<<<<<<< HEAD
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

    // ROLE 테이블과의 외래키 관계 (FK_USERS_ROLE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;
=======
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor
public class Users {

    @Id
    @Column(name = "userId", length = 15)
    private String userId;

    @Column(name = "roleId", nullable = false)
    private Integer roleId;
>>>>>>> 4d312dd5a9789eaefeb3a89b38ccdbb0ee4c1fea

    @Column(name = "userName", nullable = false, length = 34)
    private String userName;

    @Column(name = "userPassword", nullable = false, length = 255)
    private String userPassword;

    @Column(name = "userIntroduce", length = 100)
    private String userIntroduce;

    @Column(name = "userMbti", columnDefinition = "CHAR(4)")
    private String userMbti;

<<<<<<< HEAD
    @CreationTimestamp
    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;
=======
    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
>>>>>>> 4d312dd5a9789eaefeb3a89b38ccdbb0ee4c1fea

    @UpdateTimestamp
    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;
<<<<<<< HEAD
}
=======
}
>>>>>>> 4d312dd5a9789eaefeb3a89b38ccdbb0ee4c1fea
