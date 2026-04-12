package com.reverse.nsu.entity;

import lombok.*; // Lombok의 모든 기능을 사용하기 위해 추가
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "RECRUITMENT")
@Getter // Lombok이 자동으로 getter 생성
@Setter // Lombok이 자동으로 setter 생성
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruitment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitmentId") // DB 컬럼명 매핑
    private Integer id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "applyStartDate", nullable = false)
    private LocalDateTime applyStartDate;

    @Column(name = "applyEndDate", nullable = false)
    private LocalDateTime applyEndDate;

    @Column(name = "isActive", nullable = false)
    private Boolean isActive;

    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;

    // 비즈니스 로직은 기존대로 유지
    public void update(String title, String description) {
        this.title = title;
        this.description = description;
    }
}