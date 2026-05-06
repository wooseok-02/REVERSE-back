package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Recruitment;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentRequestDto {

    // --- 모집 공고 관리 및 기본 정보 ---
    @NotNull(message = "모집 공고 ID는 필수입니다.")
    private Integer recruitmentId;

    private String title;
    private String description;
    private LocalDateTime applyStartDate;
    private LocalDateTime applyEndDate;
    private Boolean isActive;
    private String updatedBy;

    // --- 상세 페이지(Hero 섹션)용 정보 ---
    private String heroYear;
    private String heroTitle;
    private String heroSubTitle;
    private String heroBgUrl;
    private String heroBtnText;

    // --- [화면정의서 반영] 신청자 상세 정보 입력란 ---

    @NotBlank(message = "이름을 입력해주세요.")
    private String studentName;

    @NotBlank(message = "학번을 입력해주세요.")
    private String studentId;

    @NotBlank(message = "전공을 입력해주세요.")
    private String major;

    @NotNull(message = "학년을 선택해주세요.")
    private Byte grade;

    // 정의서 요구사항: 000-0000-0000 형식 확인
    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호는 000-0000-0000 형식으로 입력해주세요.")
    private String phoneNumber;

    // 정의서 요구사항: 이메일 도메인 확인 (에러 문구: 그런 도메인 없다 돌아가라.)
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "그런 도메인 없다 돌아가라.")
    private String email;

    @NotNull(message = "면접 일정을 선택해주세요.")
    private Integer interviewSlotId;

    private String portfolioUrl;

    // 정의서 요구사항: 개인정보 수집 미동의 시 제출 불가
    @NotNull(message = "개인정보 수집 및 이용 동의 여부가 필요합니다.")
    @AssertTrue(message = "개인정보 수집 및 이용에 동의해야 합니다.")
    private Boolean termsAgreed;

    /**
     * DTO를 Recruitment 엔티티로 변환 (공고 생성/수정 시 사용)
     */
    public Recruitment toEntity() {
        return Recruitment.builder()
                .title(this.title)
                .description(this.description)
                .applyStartDate(this.applyStartDate)
                .applyEndDate(this.applyEndDate)
                .isActive(this.isActive != null ? this.isActive : true)
                .updatedBy(this.updatedBy)
                .build();
    }
}