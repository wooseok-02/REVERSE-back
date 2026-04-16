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
    @Column(name = "galleryId")
    private Integer id;

    // 연결된 모집 페이지 ID
    @Column(name = "pageId", nullable = false)
    private Integer pageId;

    // 이미지 경로 (TEXT 타입)
    @Column(name = "imageUrl", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    // 이미지 설명 (Alt 텍스트 등)
    @Column(name = "imageDesc", length = 100)
    private String imageDesc;

    // 태그 (스터디, 세미나, 대외활동 등)
    @Column(name = "tag", length = 20, nullable = false)
    private String tag;

    // 정렬 순서
    @Column(name = "sortOrder", nullable = false)
    private Integer sortOrder;

    // 노출 여부 (TINYINT(1) -> Boolean)
    @Column(name = "isVisible", nullable = false)
    private Boolean isVisible;

    // 최종 수정자 ID
    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;
}