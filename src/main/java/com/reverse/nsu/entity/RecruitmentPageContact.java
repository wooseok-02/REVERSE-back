package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "RECRUITMENT_PAGE_CONTACT")
public class RecruitmentPageContact extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contactId") // id -> contactId로 수정
    private Integer contactId;

    // Integer pageId -> RecruitmentPage 객체 참조로 수정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pageId", nullable = false)
    private RecruitmentPage recruitmentPage;

    @Column(name = "contactType", length = 20, nullable = false)
    private String contactType;

    @Column(name = "label", length = 50, nullable = false)
    private String label;

    @Column(name = "value", length = 255, nullable = false)
    private String value;

    @Column(name = "subValue", length = 255)
    private String subValue;

    @Column(name = "sortOrder", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0; // 기본값 0 추가

    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;
}