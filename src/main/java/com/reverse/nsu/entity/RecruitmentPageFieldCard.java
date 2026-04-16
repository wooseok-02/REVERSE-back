package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "RECRUITMENT_PAGE_FIELD_CARD", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_FIELD_CARD_PAGE_FIELD", columnNames = {"pageId", "applyField"})
})
public class RecruitmentPageFieldCard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cardId")
    private Integer id;

    @Column(name = "pageId", nullable = false)
    private Integer pageId;

    // 모집 분야 (예: 메인프로젝트, 토이프로젝트, 스터디)
    @Column(name = "applyField", length = 20, nullable = false)
    private String applyField;

    @Column(name = "cardTitle", length = 100, nullable = false)
    private String cardTitle;

    @Column(name = "cardSubTitle", length = 100)
    private String cardSubTitle;

    @Column(name = "cardDesc", length = 500)
    private String cardDesc;

    // 이미지 URL (TEXT 타입이므로 length 없이 매핑 가능)
    @Column(name = "imageUrl", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "sortOrder", nullable = false)
    private Integer sortOrder;

    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;
}