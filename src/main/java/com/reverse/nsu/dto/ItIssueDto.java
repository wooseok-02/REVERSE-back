package com.reverse.nsu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItIssueDto {
    private final String title;
    private final String imageUrl;
    private final String articleUrl;
}
