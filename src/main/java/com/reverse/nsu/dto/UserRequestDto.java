package com.reverse.nsu.dto;
import lombok.*;

public class UserRequestDto {
    @Getter
    public static class FindPasswordRequest {
            private String userId;
            private String email;
    }
}


