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
    @Column(name = "introId") // id -> introId로 수정
    private Integer introId;

    // Integer pageId -> RecruitmentPage 객체 참조로 수정 (FK 관계 반영)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pageId", nullable = false)
    private RecruitmentPage recruitmentPage;

    @Column(name = "sortOrder", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "contents", length = 500, nullable = false)
    private String contents;

    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;
}