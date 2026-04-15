package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "TERMS")
@Getter @Setter
public class Terms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "termsId")
    private Integer termsId;

    @Column(name = "sortOrder")
    private Integer sortOrder;

    @Column(name = "version", unique = true, nullable = false, length = 20)
    private String version;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "contents", columnDefinition = "TEXT", nullable = false)
    private String contents;

    @Column(name = "isCurrent")
    private Boolean isCurrent;

    @Column(name = "updatedBy", length = 20)
    private String updatedBy;

    @Column(name = "createdDate")
    private LocalDateTime createdDate = LocalDateTime.now();
}