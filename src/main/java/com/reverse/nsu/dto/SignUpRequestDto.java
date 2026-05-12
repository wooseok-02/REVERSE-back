package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {
    private String userId;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userIntroduce;
    private String userMbti;
    private List<ConsentDto> consents;
}
