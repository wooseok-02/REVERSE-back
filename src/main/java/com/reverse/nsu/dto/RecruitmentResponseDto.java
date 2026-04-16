package com.reverse.nsu.dto;

import lombok.*;
import java.util.List;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentResponseDto {

    private Integer recruitmentId;
    private String title;
    private PageDetails page;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageDetails {
        private String year;
        private String title;
        private String subtitle;
        private String heroImageUrl;
        private String heroBtnText;
        private List<IntroDetails> intros;
        private List<CardDetails> cards;
        private List<GalleryDetails> galleries;
        private List<ContactDetails> contacts;
        private List<SlotDetails> interviewSlots; // 여기서 에러 났던 부분!
    }

    @Builder @Getter @NoArgsConstructor @AllArgsConstructor
    public static class IntroDetails {
        private String contents;
        private Integer sortOrder;
    }

    @Builder @Getter @NoArgsConstructor @AllArgsConstructor
    public static class CardDetails {
        private String applyField;
        private String title;
        private String subTitle;
        private String desc;
        private String imageUrl;
    }

    @Builder @Getter @NoArgsConstructor @AllArgsConstructor
    public static class GalleryDetails {
        private String imageUrl;
        private String imageDesc;
        private String tag;
    }

    @Builder @Getter @NoArgsConstructor @AllArgsConstructor
    public static class ContactDetails {
        private String type;
        private String label;
        private String value;
        private String subValue;
    }

    @Builder @Getter @NoArgsConstructor @AllArgsConstructor
    public static class SlotDetails {
        private Integer slotId;
        private String date;
        private String time;
    }
}