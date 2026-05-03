package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Recruitment;
import com.reverse.nsu.entity.RecruitmentApplication;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder // 서비스 레이어에서 빌더를 사용할 수 있도록 추가
@NoArgsConstructor
@AllArgsConstructor // 빌더 사용을 위해 추가
public class ApplicationRequestDto {

    @NotNull(message = "공고 ID는 필수입니다.")
    private Integer recruitmentId;

    @NotBlank(message = "이름을 입력해주세요.")
    private String applicantName;

    @NotBlank(message = "학과를 입력해주세요.")
    private String department;

    @NotBlank(message = "학번을 입력해주세요.")
    private String studentNumber;

    // [수정] 서비스 코드와 엔티티 필드명에 맞춰 userPhone -> phoneNumber로 변경
    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "휴대폰 번호는 000-0000-0000 형식으로 입력해주세요.")
    private String phoneNumber;

    @NotNull(message = "학년을 선택해주세요.")
    @Min(value = 1, message = "학년은 1학년부터 입력 가능합니다.")
    @Max(value = 5, message = "학년은 5학년을 초과할 수 없습니다.")
    private Byte grade;

    // [수정] 서비스 코드와 엔티티 필드명에 맞춰 userEmail -> email로 변경
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    private String portfolioUrl;

    @AssertTrue(message = "개인정보 수집 동의가 필요합니다.")
    private Boolean termsAgreed;

    private List<String> categories;

    /**
     * DTO를 엔티티로 변환하는 메서드
     * [수정] 엔티티 필드명과 일치하도록 매핑 (phoneNumber, email)
     */
    public RecruitmentApplication toEntity(Recruitment recruitment) {
        return RecruitmentApplication.builder()
                .recruitment(recruitment)
                .applicantName(this.applicantName)
                .studentNumber(this.studentNumber)
                .department(this.department)
                .grade(this.grade)
                .phoneNumber(this.phoneNumber) // 변경됨
                .email(this.email)             // 변경됨
                .termsAgreed(this.termsAgreed ? 1 : 0) // Integer 타입일 경우 대응
                .status("PENDING")
                .build();
    }
}