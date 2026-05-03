package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class RecruitmentStatusRequestDto {
    private Integer recruitmentId;
    private String userName;
    private String userMajor;
    private String userPhone;
    private String userEmail;
    private String portfolioUrl;
    private List<Integer> applyFieldIds;    // 지원 분야 ID들
    private List<Integer> selectedSlotIds;  // 선택한 면접 슬롯 ID들
}