package com.reverse.nsu.service;

import com.reverse.nsu.entity.Users;
import com.reverse.nsu.repository.UsersRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    // 인증번호 임시 저장소 (Key: 이메일, Value: 인증번호)
    private final Map<String, String> verificationStorage = new ConcurrentHashMap<>();

    // 인증 완료 여부 저장소 (Key: 이메일, Value: 완료 여부)
    private final Map<String, Boolean> verifiedUsers = new ConcurrentHashMap<>();

    /**
     * 1단계: 아이디/이메일 검증 후 인증번호 발송
     */
    @Transactional(readOnly = true)
    public void sendVerificationCode(String userId, String email) {
        // [수정 포인트 1]: 이미지 확인 결과, USERS 테이블의 'userEmail' 컬럼을 직접 사용해야 함
        // findByUserIdAndUserEmail (또는 기존 Repository 메서드 확인 필요)
        usersRepository.findByUserIdAndUserEmail(userId, email)
                .orElseThrow(() -> new RuntimeException("입력하신 정보와 일치하는 회원이 없습니다."));

        // 2. 6자리 인증번호 생성
        String authCode = String.valueOf((int)(Math.random() * 899999) + 100000);

        // 3. 메모리에 저장
        verificationStorage.put(email, authCode);

        // 4. 메일 발송
        sendMail(email, "이메일 인증 번호입니다.\n인증번호: " + authCode, "[REVERSE] 비밀번호 찾기 인증번호");
    }

    /**
     * 2단계: 인증번호 일치 여부 확인
     */
    public void verifyCode(String email, String inputCode) {
        String savedCode = verificationStorage.get(email);

        if (savedCode == null) {
            throw new RuntimeException("유효한 인증 정보가 없거나 시간이 만료되었습니다.");
        }

        if (!savedCode.equals(inputCode)) {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }

        // 인증 성공 시 기록
        verificationStorage.remove(email);
        verifiedUsers.put(email, true);
    }

    /**
     * 3단계: 임시 비밀번호 발급 및 DB 업데이트
     */
    @Transactional
    public void issueTempPassword(String userId, String email) {
        // 인증 여부 확인
        if (Boolean.FALSE.equals(verifiedUsers.get(email))) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        // [수정 포인트 2]: 여기도 동일하게 USERS 테이블의 컬럼 기준으로 조회
        Users user = usersRepository.findByUserIdAndUserEmail(userId, email)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        // 8자리 임시 비밀번호 생성
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        // BCrypt 암호화 후 저장
        user.setUserPassword(passwordEncoder.encode(tempPassword));
        usersRepository.save(user); // @Transactional이 있지만 명시적 저장 권장

        // 인증 상태 삭제
        verifiedUsers.remove(email);

        sendMail(email, "임시 비밀번호가 발급되었습니다.\n임시 비밀번호: " + tempPassword + "\n로그인 후 반드시 비밀번호를 변경해주세요.", "[REVERSE] 임시 비밀번호 발급 안내");
    }

    /**
     * 공통 메일 발송 로직
     */
    private void sendMail(String toEmail, String content, String subject) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            String cleanTo = toEmail.trim().replaceAll("\\s+", "");

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(cleanTo);
            helper.setFrom("reverse.nsu@gmail.com");
            helper.setSubject(subject);
            helper.setText(content, false);

            mailSender.send(mimeMessage);
            log.info("메일 발송 성공: {}", cleanTo);

        } catch (Exception e) {
            log.error("메일 발송 실패: {}", e.getMessage());
            throw new RuntimeException("메일 서버 연결에 실패했습니다.");
        }
    }
}