package com.reverse.nsu.service;

import com.reverse.nsu.dto.LoginRequestDto;
import com.reverse.nsu.dto.TokenResponseDto;
import com.reverse.nsu.entity.UserToken;
import com.reverse.nsu.entity.Users;
import com.reverse.nsu.repository.UserTokenRepository;
import com.reverse.nsu.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final UserTokenRepository userTokenRepository;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public TokenResponseDto login(LoginRequestDto dto, String ipAddress, String deviceInfo) {
        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(dto.getUserPassword(), user.getUserPassword())) {
            throw new RuntimeException("비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtProvider.generateAccessToken(user.getUserId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUserId());
        LocalDateTime accessExpiry = jwtProvider.getAccessTokenExpiry();
        LocalDateTime refreshExpiry = jwtProvider.getRefreshTokenExpiry();

        UserToken tokenEntity = UserToken.create(
                user.getUserId(), accessToken, refreshToken,
                accessExpiry, refreshExpiry, deviceInfo, ipAddress
        );
        userTokenRepository.save(tokenEntity);

        return TokenResponseDto.of(accessToken, refreshToken, accessExpiry, refreshExpiry);
    }

    @Transactional
    public TokenResponseDto refresh(String refreshToken) {
        UserToken tokenEntity = userTokenRepository
                .findByRefreshTokenAndIsRevoked(refreshToken, false)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));

        if (tokenEntity.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("리프레시 토큰이 만료되었습니다.");
        }

        String newAccessToken = jwtProvider.generateAccessToken(tokenEntity.getUserId());
        LocalDateTime newAccessExpiry = jwtProvider.getAccessTokenExpiry();

        tokenEntity.updateAccessToken(newAccessToken, newAccessExpiry);
        userTokenRepository.save(tokenEntity);

        return TokenResponseDto.of(
                newAccessToken, tokenEntity.getRefreshToken(),
                newAccessExpiry, tokenEntity.getRefreshTokenExpiry()
        );
    }

    @Transactional
    public void logout(String refreshToken) {
        UserToken tokenEntity = userTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("토큰을 찾을 수 없습니다."));
        tokenEntity.revoke();
        userTokenRepository.save(tokenEntity);
    }
}
