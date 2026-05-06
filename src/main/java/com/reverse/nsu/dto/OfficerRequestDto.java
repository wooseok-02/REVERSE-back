package com.reverse.nsu.dto;

import lombok.*;

@Getter
@AllArgsConstructor @NoArgsConstructor
public class OfficerRequestDto {

    private String name;        // 이름
    private Integer generation; // 기수
    private String role;        // 직책
    private String department;  // 소속 파트
    private String email;       // 이메일
    private String photoUrl;    // R2에서 받은 이미지 URL
    private Integer sortOrder;  // 노출 순서
    private Boolean isVisible;  // 노출 여부
    private String updatedBy;   // 수정 관리자 ID
}