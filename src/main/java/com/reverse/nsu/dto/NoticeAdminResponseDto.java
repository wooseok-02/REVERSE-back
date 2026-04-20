package com.reverse.nsu.dto;

import lombok.Getter;

@Getter
public class NoticeAdminResponseDto {
    private final Integer noticeId;

    public NoticeAdminResponseDto(Integer noticeId) {
        this.noticeId = noticeId;
    }
}