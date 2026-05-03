package com.reverse.nsu.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationInterviewScheduleRequestDto {
    private Integer applicationId; // 지원서 ID
    private Integer slotId;        // 선택한 면접 타임슬롯 ID
}