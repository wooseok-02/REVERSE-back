package com.reverse.nsu.dto;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor @NoArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime accessTokenExpiry;
    private LocalDateTime refreshTokenExpiry;

    public static TokenResponseDto of(String accessToken, String refreshToken,
                                      LocalDateTime accessTokenExpiry, LocalDateTime refreshTokenExpiry) {
        return new TokenResponseDto(accessToken, refreshToken, accessTokenExpiry, refreshTokenExpiry);
    }
}
