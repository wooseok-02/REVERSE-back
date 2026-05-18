package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectApplyRequestDto {
    private String email;
    private String availableDate; // "2026-05-20" 형태로 프론트에서 전송
    private String availableTime; // "오후 6시" 등 드롭다운 선택 값
    private Boolean privacyAgreement;
}