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
    @Column(name = "cardId") // id -> cardId로 수정
    private Integer cardId;

    // Integer pageId -> RecruitmentPage 객체 참조로 수정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pageId", nullable = false)
    private RecruitmentPage recruitmentPage;

    @Column(name = "applyField", length = 20, nullable = false)
    private String applyField;

    @Column(name = "cardTitle", length = 100, nullable = false)
    private String cardTitle;

    @Column(name = "cardSubTitle", length = 100)
    private String cardSubTitle;

    @Column(name = "cardDesc", length = 500)
    private String cardDesc;

    @Column(name = "imageUrl", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "sortOrder", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0; // 기본값 0 추가

    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;
}