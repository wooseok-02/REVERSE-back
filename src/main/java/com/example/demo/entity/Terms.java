package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "TERMS") // 서버 DB 테이블명이 대문자이므로 명시
public class Terms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "termsId") // DB 컬럼명: termsId
    private Integer termsId;

    @Column(name = "sortOrder")
    private Integer sortOrder = 0;

    @Column(name = "version", length = 20)
    private String version;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "contents", columnDefinition = "TEXT")
    private String contents;

    @Column(name = "isCurrent")
    private Boolean isCurrent = false;

    @Column(name = "updatedBy", length = 15)
    private String updatedBy;

    @Column(name = "createdDate", updatable = false)
    private LocalDateTime createdDate;
}