package com.reverse.nsu.service;

import com.reverse.nsu.entity.Users;
import com.reverse.nsu.repository.UsersRepository;
import jakarta.mail.internet.MimeMessage; // jakarta 패키지 확인
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Setter
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Transactional
    public void sendTempPassword(String userId, String email) {
        // 1. DB 매칭 확인 (userId와 Officer 테이블의 email)
        Users user = usersRepository.findByUserIdAndOfficerEmail(userId, email)
                .orElseThrow(() -> new RuntimeException("입력하신 정보와 일치하는 회원이 없습니다."));

        // 2. 임시 비밀번호 생성 (8자리)
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        // 3. 비밀번호 암호화 후 DB 업데이트
        // Users 엔티티 필드명이 userPassword이므로 setUserPassword 호출
        user.setUserPassword(passwordEncoder.encode(tempPassword));

        // 4. 메일 발송
        sendMail(email, tempPassword);
    }

    private void sendMail(String toEmail, String tempPassword) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // 1. 현재 들어온 이메일의 정체를 파악합니다.
            System.out.println("----- 메일 발송 디버그 시작 -----");
            System.out.println("원본 이메일: [" + toEmail + "]");
            System.out.println("문자열 길이: " + (toEmail != null ? toEmail.length() : 0));

            // 2. 혹시 모를 모든 공백과 제어문자를 싹 제거합니다.
            String cleanTo = toEmail.trim().replaceAll("\\s+", "");
            System.out.println("정제된 이메일: [" + cleanTo + "]");
            System.out.println("----- 메일 발송 디버그 끝 -----");

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setTo(cleanTo);
            helper.setFrom("reverse.nsu@gmail.com"); // yml의 username과 동일하게!
            helper.setSubject("임시 비밀번호");
            helper.setText("Password: " + tempPassword, false);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("메일 발송 실패: " + e.getMessage());
        }
    }


}