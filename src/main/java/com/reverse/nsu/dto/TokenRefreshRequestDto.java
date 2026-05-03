package com.reverse.nsu.dto;

import lombok.*;

@Getter
@AllArgsConstructor @NoArgsConstructor
public class TokenRefreshRequestDto {
    private String refreshToken;
}
