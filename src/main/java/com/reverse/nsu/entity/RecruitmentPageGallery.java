package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "RECRUITMENT_PAGE_GALLERY")
public class RecruitmentPageGallery extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "galleryId") // id -> galleryId로 수정
    private Integer galleryId;

    // Integer pageId -> RecruitmentPage 객체 참조로 수정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pageId", nullable = false)
    private RecruitmentPage recruitmentPage;

    @Column(name = "imageUrl", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @Column(name = "imageDesc", length = 100)
    private String imageDesc;

    @Column(name = "tag", length = 20, nullable = false)
    private String tag;

    @Column(name = "sortOrder", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0; // 기본값 0 추가

    @Column(name = "isVisible", columnDefinition = "TINYINT(1)", nullable = false)
    @Builder.Default
    private Boolean isVisible = true; // 기본값 true(1) 추가

    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;
}