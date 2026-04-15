package com.reverse.nsu.service;

import com.reverse.nsu.dto.RecruitmentRequestDto;
import com.reverse.nsu.dto.RecruitmentResponseDto;
import com.reverse.nsu.dto.RecruitmentStatusRequestDto;
import com.reverse.nsu.entity.Recruitment;
import com.reverse.nsu.entity.RecruitmentNotifyEmail;
import com.reverse.nsu.entity.Users; // 추가
import com.reverse.nsu.repository.RecruitmentNotifyEmailRepository;
import com.reverse.nsu.repository.RecruitmentRepository;
import com.reverse.nsu.repository.UserRepository; // 추가
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException; // 예외 추가
import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentNotifyEmailRepository notifyEmailRepository;
    private final UserRepository userRepository; // 1. 주입 추가
    private final JavaMailSender mailSender;

    @Transactional(readOnly = true)
    public List<RecruitmentResponseDto> getAll() {
        return recruitmentRepository.findAll().stream()
                .map(RecruitmentResponseDto::from) // DTO의 from 메서드 사용
                .collect(Collectors.toList());
    }

    // 2. 특정 ID 모집 공고 조회
    @Transactional(readOnly = true)
    public RecruitmentResponseDto getById(Integer id) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공고가 없습니다. id=" + id));
        return RecruitmentResponseDto.from(recruitment);
    }

    @Transactional
    public RecruitmentResponseDto save(RecruitmentRequestDto dto) {
        // 1. 공고 저장
        Recruitment recruitment = recruitmentRepository.save(dto.toEntity());

        // 2. 알림 구독자 전원 조회
        List<RecruitmentNotifyEmail> subscribers = notifyEmailRepository.findAll();

        // 3. 메일 발송 로직
        for (RecruitmentNotifyEmail subscriber : subscribers) {
            String targetEmail = subscriber.getEmail();

            if (targetEmail == null || !targetEmail.contains("@")) {
                System.out.println("유효하지 않은 이메일 건너뜀: " + targetEmail);
                continue;
            }

            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("reverse.nsu@gmail.com");
                message.setTo(subscriber.getEmail());
                message.setSubject("[REVERSE] " + recruitment.getTitle() + " 공고가 등록되었습니다!");
                message.setText("안녕하세요. REVERSE입니다.\n\n새로운 모집 공고가 등록되었습니다.\n"
                        + "제목: " + recruitment.getTitle() + "\n"
                        + "지금 바로 홈페이지에서 확인해보세요!");
                mailSender.send(message);

            } catch (Exception e) {
                System.out.println("메일 발송 실패: " + subscriber.getEmail());
                e.printStackTrace();
            }
        }

        return RecruitmentResponseDto.from(recruitment);
    }


    // 수정
    @Transactional
    public RecruitmentResponseDto update(Integer id, RecruitmentRequestDto dto) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공고가 없습니다. id=" + id));

        // Entity의 update 메서드 인자 확인 필요 (title, description)
        recruitment.update(dto.getTitle(), dto.getDescription());

        return RecruitmentResponseDto.from(recruitment);
    }

    // 삭제
    @Transactional
    public void delete(Integer id) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공고가 없습니다. id=" + id));
        recruitmentRepository.delete(recruitment);
    }

    // 관리자 모집 공고 상태 및 노출 여부 변경
    @Transactional
    public RecruitmentResponseDto updateStatus(Integer id, Integer roleId, RecruitmentStatusRequestDto dto) {

        // 1. roleId가 1(관리자)인지 확인
        if (roleId == null || roleId != 1) {
            throw new RuntimeException("관리자 권한이 없습니다.");
        }

        // 2. 공고 조회 (파라미터 id 사용)
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공고가 없습니다. id=" + id));

        // 3. isActive(노출 여부) 변경
        // DTO에 isActive 필드가 있는지, Getter 이름이 getIsActive인지 확인하세요!
        recruitment.setIsActive(dto.getIsActive());

        // 4. 결과를 반환하고 싶다면 return 타입을 맞춰줍니다.
        return RecruitmentResponseDto.from(recruitment);
    }
}