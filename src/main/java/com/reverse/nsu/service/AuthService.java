package com.reverse.nsu.service;

import com.reverse.nsu.dto.ConsentDto;
import com.reverse.nsu.dto.EmailSendRequestDto;
import com.reverse.nsu.dto.EmailVerifyRequestDto;
import com.reverse.nsu.dto.LoginRequestDto;
import com.reverse.nsu.dto.SignUpRequestDto;
import com.reverse.nsu.dto.TokenResponseDto;
import com.reverse.nsu.entity.ConsentItem;
import com.reverse.nsu.entity.EmailVerification;
import com.reverse.nsu.entity.Role;
import com.reverse.nsu.entity.UserConsent;
import com.reverse.nsu.entity.UserToken;
import com.reverse.nsu.entity.Users;
import com.reverse.nsu.repository.ConsentItemRepository;
import com.reverse.nsu.repository.EmailVerificationRepository;
import com.reverse.nsu.repository.RoleRepository;
import com.reverse.nsu.repository.UserConsentRepository;
import com.reverse.nsu.repository.UserTokenRepository;
import com.reverse.nsu.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final UserTokenRepository userTokenRepository;
    private final RoleRepository roleRepository;
    private final ConsentItemRepository consentItemRepository;
    private final UserConsentRepository userConsentRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // ── 이메일 인증번호 전송 ──────────────────────────────────────────────────

    @Transactional
    public void sendVerificationCode(EmailSendRequestDto dto) {
        // 기존 미인증 코드 전체 삭제 후 새 코드 발급
        List<EmailVerification> existing = emailVerificationRepository.findAllByEmail(dto.getEmail());
        emailVerificationRepository.deleteAll(existing);

        String code = generateCode();
        emailVerificationRepository.save(EmailVerification.create(dto.getEmail(), code));
        emailService.sendVerificationCode(dto.getEmail(), code);
    }

    @Transactional
    public void verifyEmailCode(EmailVerifyRequestDto dto) {
        EmailVerification verification = emailVerificationRepository
                .findTopByEmailAndIsVerifiedFalseOrderByCreatedDateDesc(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("인증번호를 찾을 수 없습니다."));

        if (verification.isExpired()) {
            throw new RuntimeException("인증번호가 만료되었습니다.");
        }
        if (!verification.getCode().equals(dto.getCode())) {
            throw new RuntimeException("인증번호가 올바르지 않습니다.");
        }

        verification.verify();
        emailVerificationRepository.save(verification);
    }

    // ── 회원가입 ──────────────────────────────────────────────────────────────

    @Transactional
    public void register(SignUpRequestDto dto) {
        if (usersRepository.existsById(dto.getUserId())) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        if (!emailVerificationRepository.existsByEmailAndIsVerifiedTrue(dto.getUserEmail())) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        List<ConsentItem> requiredItems = consentItemRepository.findAllByIsActiveTrueOrderBySortOrderAsc()
                .stream().filter(ConsentItem::getIsRequired).toList();

        for (ConsentItem required : requiredItems) {
            boolean agreed = dto.getConsents() != null && dto.getConsents().stream()
                    .anyMatch(c -> c.getConsentItemId().equals(required.getConsentItemId()) && Boolean.TRUE.equals(c.getIsAgreed()));
            if (!agreed) {
                throw new RuntimeException("필수 약관에 동의해야 합니다: " + required.getConsentName());
            }
        }

        Role role = roleRepository.findByRoleName("GUEST")
                .orElseThrow(() -> new RuntimeException("기본 역할을 찾을 수 없습니다."));

        Users user = Users.builder()
                .userId(dto.getUserId())
                .userName(dto.getUserName())
                .userEmail(dto.getUserEmail())
                .userPassword(passwordEncoder.encode(dto.getUserPassword()))
                .userIntroduce(dto.getUserIntroduce())
                .userMbti(dto.getUserMbti())
                .role(role)
                .build();

        usersRepository.save(user);

        if (dto.getConsents() != null) {
            for (ConsentDto consentDto : dto.getConsents()) {
                ConsentItem item = consentItemRepository.findById(consentDto.getConsentItemId())
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 약관 항목입니다: " + consentDto.getConsentItemId()));
                userConsentRepository.save(UserConsent.of(user.getUserId(), item, consentDto.getIsAgreed()));
            }
        }
    }

    // ── 로그인 ────────────────────────────────────────────────────────────────

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

    // ── 토큰 재발급 ───────────────────────────────────────────────────────────

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

    // ── 로그아웃 ──────────────────────────────────────────────────────────────

    @Transactional
    public void logout(String refreshToken) {
        UserToken tokenEntity = userTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("토큰을 찾을 수 없습니다."));
        tokenEntity.revoke();
        userTokenRepository.save(tokenEntity);
    }

    // ── 내부 유틸 ─────────────────────────────────────────────────────────────

    private String generateCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }
}
