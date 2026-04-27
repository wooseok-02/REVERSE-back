package com.reverse.nsu.service;

import com.reverse.nsu.entity.Users;
import com.reverse.nsu.repository.UsersRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    // 인증번호 임시 저장소 (Key: 이메일, Value: 인증번호)
    // 메모리 누수를 방지하기 위해 실제 서비스에서는 Redis 사용을 권장합니다.
    private final Map<String, String> verificationStorage = new ConcurrentHashMap<>();

    private final Map<String, Boolean> verifiedUsers = new ConcurrentHashMap<>();
    /**
     * 1단계: 아이디/이메일 검증 후 인증번호 발송
     */
    @Transactional(readOnly = true)
    public void sendVerificationCode(String userId, String email) {
        // 1. DB 매칭 확인 (USERS - OFFICER 조인 쿼리 사용)
        usersRepository.findByUserIdAndOfficerEmail(userId, email)
                .orElseThrow(() -> new RuntimeException("입력하신 정보와 일치하는 회원이 없습니다."));

        // 2. 6자리 인증번호 생성
        String authCode = String.valueOf((int)(Math.random() * 899999) + 100000);

        // 3. 메모리에 저장 (이메일을 키로 사용)
        verificationStorage.put(email, authCode);

        // 4. 메일 발송
        sendMail(email, "인증번호: " + authCode, "이메일 인증 번호입니다.");
    }

    /**
     * 2단계: 인증번호 일치 여부 확인
     */
    public void verifyCode(String email, String inputCode) {
        if (!verificationStorage.containsKey(email)) {
            throw new RuntimeException("유효한 인증 정보가 없습니다. 먼저 이메일 인증을 요청해주세요.");
        }

        String savedCode = verificationStorage.get(email);
        if (!savedCode.equals(inputCode)) {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }

        // [중요] 인증 성공 시, 발송 대기중인 번호는 지우고 '인증 완료' 목록에 등록
        verificationStorage.remove(email);
        verifiedUsers.put(email, true);
    }

    /**
     * 3단계: 임시 비밀번호 발급 및 DB 업데이트
     */
    @Transactional
    public void issueTempPassword(String userId, String email) {
        // [중요] 인증 완료 목록에 이 이메일이 있는지 확인
        if (verifiedUsers.get(email) == null || !verifiedUsers.get(email)) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        // 유저 조회 및 비밀번호 변경 로직
        Users user = usersRepository.findByUserIdAndOfficerEmail(userId, email)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setUserPassword(passwordEncoder.encode(tempPassword));

        // 발급 완료 후 인증 상태 삭제 (재사용 방지)
        verifiedUsers.remove(email);

        sendMail(email, "임시 비밀번호: " + tempPassword, "임시 비밀번호 발급 안내");
    }

    /**
     * 공통 메일 발송 로직
     */
    private void sendMail(String toEmail, String content, String subject) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // 공백 및 제어문자 제거
            String cleanTo = toEmail.trim().replaceAll("\\s+", "");

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(cleanTo);
            helper.setFrom("reverse.nsu@gmail.com");
            helper.setSubject(subject);
            helper.setText(content, false);

            mailSender.send(mimeMessage);
            System.out.println("메일 발송 완료: " + cleanTo + " / 제목: " + subject);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("메일 발송 실패: " + e.getMessage());
        }
    }
}