package com.reverse.nsu.service;

import com.reverse.nsu.dto.MeResponseDto;
import com.reverse.nsu.entity.Users;
import com.reverse.nsu.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final JwtProvider jwtProvider;

    public MeResponseDto getMe(String token) {
        // Bearer 제거
        String jwt = token.substring(7);
        String userId = jwtProvider.getUserId(jwt);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return MeResponseDto.from(user);
    }
}