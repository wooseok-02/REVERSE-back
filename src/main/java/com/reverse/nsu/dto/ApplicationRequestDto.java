package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationRequestDto {
    private Integer recruitmentId;
    private String userName;      // 엔티티의 userName과 매칭
    private String userMajor;     // 엔티티의 department(userMajor)와 매칭
    private String studentNumber;
    private String userPhone;     // 엔티티의 phoneNumber(userPhone)와 매칭
    private Byte grade;
    private String userEmail;     // 엔티티의 email(userEmail)과 매칭
    private String portfolioUrl;
    private Boolean termsAgreed;
}