package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Recruitment;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitmentRequestDto {

    private Integer recruitmentId;
    private String title;
    private String description;
    private LocalDateTime applyStartDate;
    private LocalDateTime applyEndDate;
    private Boolean isActive;
    private String updatedBy;

    // 상세 페이지 수정용
    @Getter @Setter
    public static class PageUpdate {
        private Integer roleId;
        private String adminId;
        private String heroYear;
        private String heroTitle;
        private String heroSubTitle;
        private String heroBtnText;
        private String heroBgUrl;
        private List<IntroDto> intros;
        private List<CardDto> cards;
        private List<GalleryDto> galleries;
        private List<ContactDto> contacts;
    }

    // [신규] 면접 슬롯 수정용
    @Getter @Setter
    public static class InterviewSlotUpdate {
        private Integer roleId;
        private String adminId;
        private List<SlotDto> slots;
    }

    @Getter @Setter
    public static class SlotDto {
        private LocalDate slotDate;
        private Integer capacity;
    }

    // 리스트 항목 DTO들
    @Getter @Setter public static class IntroDto { private String contents; private Integer sortOrder; }
    @Getter @Setter public static class CardDto { private String applyField; private String cardTitle; private String cardSubTitle; private String cardDesc; private String imageUrl; private Integer sortOrder; }
    @Getter @Setter public static class GalleryDto { private String imageUrl; private String imageDesc; private String tag; private Integer sortOrder; }
    @Getter @Setter public static class ContactDto { private String contactType; private String label; private String value; private String subValue; private Integer sortOrder; }

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