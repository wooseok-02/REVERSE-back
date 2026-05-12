package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConsentDto {
    private Integer consentItemId;
    private Boolean isAgreed;
}
