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
    @Column(name = "contactId")
    private Integer id;

    // 연결된 모집 페이지 ID
    @Column(name = "pageId", nullable = false)
    private Integer pageId;

    // 연락처 타입 (예: SNS, PHONE, LOCATION)
    @Column(name = "contactType", length = 20, nullable = false)
    private String contactType;

    // 표시 라벨 (예: Instagram, 대표번호, 과방 위치)
    @Column(name = "label", length = 50, nullable = false)
    private String label;

    // 실제 값 (예: @nsu_reverse, 010-0000-0000)
    @Column(name = "value", nullable = false)
    private String value;

    // 추가 정보 (예: 부회장 번호, 상세 주소 등 - NULL 허용)
    @Column(name = "subValue")
    private String subValue;

    // 출력 순서
    @Column(name = "sortOrder", nullable = false)
    private Integer sortOrder;

    // 최종 수정자 ID
    @Column(name = "updatedBy", length = 15, nullable = false)
    private String updatedBy;
}