package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Entity @Table(name = "BOARD") @Getter
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer boardId;

    @Column(nullable = false, length = 20)
    private String boardName;

    @Column(length = 50)
    private String boardDescription;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime modifiedDate = LocalDateTime.now();
}