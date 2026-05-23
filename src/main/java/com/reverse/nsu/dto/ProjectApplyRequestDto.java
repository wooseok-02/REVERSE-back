package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectApplyRequestDto {
    private String email;
    private boolean privacyAgreement;
}