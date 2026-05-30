package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class VoteRequestDto {
    private String title;
    private String content;
    private LocalDateTime deadline;
    private Boolean isMultiple;
    private List<String> options;
}
