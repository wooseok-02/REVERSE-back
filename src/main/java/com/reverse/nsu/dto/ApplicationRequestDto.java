package com.reverse.nsu.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
public class ApplicationRequestDto {
    private Integer recruitmentId;
    private String applicantName;
    private String department;
    private String studentNumber;
    private String phoneNumber;
    private Byte grade;
    private String email;
    private String interviewMemo;
    private Boolean termsAgreed;
    private List<String> applyFields; // ["메인프로젝트", "스터디"] 형태로 전송
}