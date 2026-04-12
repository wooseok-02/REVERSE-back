package com.reverse.nsu.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import jakarta.persistence.*;
import java.time.LocalDateTime;

//@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(name = "createdDate", updatable = false) // DB 컬럼명에 맞춤
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modifiedDate") // DB 컬럼명에 맞춤
    private LocalDateTime updatedAt;
}