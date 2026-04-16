package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "RECRUITMENT_PAGE_INTRO")
public class RecruitmentPageIntro extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "introId", nullable = false)
    private Integer id;

    // 페이지 ID (어떤 모집 페이지의 단락인지)
    @Column(name = "pageId", nullable = false)
    private Integer pageId;

    // 정렬 순서
    @Column(name = "sortOrder", nullable = false)
    private Integer sortOrder;

    // 소개 단락 텍스트 (VARCHAR(500))
    @Column(name = "contents", length = 500, nullable = false)
    private String contents;

    // 최종 수정자 ID (VARCHAR(15))
    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;
}