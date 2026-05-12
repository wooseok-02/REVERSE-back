package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOARD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer boardId;

    @Column(nullable = false, length = 20)
    private String boardName; // 예: "공지사항", "자유", "대외활동"

    @Column(length = 50)
    private String boardDescription;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime modifiedDate = LocalDateTime.now();
}