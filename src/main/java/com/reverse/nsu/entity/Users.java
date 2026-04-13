package com.reverse.nsu.entity;

import jakarta.persistence.*;
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

    @Column(name = "userName", nullable = false, length = 34)
    private String userName;

    @Column(name = "userPassword", nullable = false, length = 255)
    private String userPassword;

    @Column(name = "userIntroduce", length = 100)
    private String userIntroduce;

    @Column(name = "userMbti", columnDefinition = "CHAR(4)")
    private String userMbti;

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;
}
