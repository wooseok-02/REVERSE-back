package com.reverse.nsu.service;

import com.reverse.nsu.dto.*;
import com.reverse.nsu.entity.*;
import com.reverse.nsu.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentPageRepository pageRepository;
    private final RecruitmentInterviewSlotRepository slotRepository;
    private final RecruitmentPageIntroRepository introRepository;
    private final RecruitmentPageFieldCardRepository cardRepository;
    private final RecruitmentPageGalleryRepository galleryRepository;
    private final RecruitmentPageContactRepository contactRepository;
    private final RecruitmentApplicationRepository applicationRepository;
    private final JavaMailSender mailSender;

    /**
     * 1. 신규 공고 생성
     */
    @Transactional
    public RecruitmentResponseDto save(RecruitmentRequestDto dto, String adminId) {
        Recruitment recruitment = dto.toEntity();
        recruitment.setUpdatedBy(adminId);
        recruitment.setIsActive(true);

        Recruitment savedRecruit = recruitmentRepository.save(recruitment);
        sendEmailToSubscribers(savedRecruit.getTitle());

        return RecruitmentResponseDto.builder()
                .recruitmentId(savedRecruit.getRecruitmentId())
                .title(savedRecruit.getTitle())
                .build();
    }

    @Transactional
    public RecruitmentResponseDto save(RecruitmentRequestDto dto) {
        return save(dto, "SYSTEM");
    }

    /**
     * 2. 공고 전체 및 상세 조회
     */
    @Transactional(readOnly = true)
    public List<RecruitmentResponseDto> getAll() {
        return recruitmentRepository.findAll().stream()
                .map(r -> RecruitmentResponseDto.builder()
                        .recruitmentId(r.getRecruitmentId())
                        .title(r.getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecruitmentResponseDto getById(Integer id) {
        Recruitment r = recruitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공고 없음"));
        return RecruitmentResponseDto.builder()
                .recruitmentId(r.getRecruitmentId())
                .title(r.getTitle())
                .build();
    }

    @Transactional(readOnly = true)
    public RecruitmentResponseDto getRecruitPage(Integer recruitmentId) {
        Recruitment recruit = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다."));

        RecruitmentPage page = pageRepository.findByRecruitment_RecruitmentId(recruitmentId).orElse(null);

        if (page == null) {
            return RecruitmentResponseDto.builder()
                    .recruitmentId(recruit.getRecruitmentId())
                    .title(recruit.getTitle())
                    .build();
        }

        Integer pageId = page.getPageId();
        return RecruitmentResponseDto.builder()
                .recruitmentId(recruit.getRecruitmentId())
                .title(recruit.getTitle())
                .page(RecruitmentResponseDto.PageDetails.builder()
                        .year(page.getHeroYear()).title(page.getHeroTitle()).subtitle(page.getHeroSubTitle()).heroImageUrl(page.getHeroBgUrl())
                        .intros(introRepository.findAllByPage_PageIdOrderBySortOrderAsc(pageId).stream().map(i -> RecruitmentResponseDto.IntroDetails.builder().contents(i.getContents()).sortOrder(i.getSortOrder()).build()).collect(Collectors.toList()))
                        .cards(cardRepository.findAllByPage_PageIdOrderBySortOrderAsc(pageId).stream().map(c -> RecruitmentResponseDto.CardDetails.builder().applyField(c.getApplyField()).title(c.getCardTitle()).subTitle(c.getCardSubTitle()).desc(c.getCardDesc()).imageUrl(c.getImageUrl()).build()).collect(Collectors.toList()))
                        .galleries(galleryRepository.findAllByPage_PageIdOrderBySortOrderAsc(pageId).stream().map(g -> RecruitmentResponseDto.GalleryDetails.builder().imageUrl(g.getImageUrl()).imageDesc(g.getImageDesc()).tag(g.getTag()).build()).collect(Collectors.toList()))
                        .contacts(contactRepository.findAllByPage_PageIdOrderBySortOrderAsc(pageId).stream().map(ct -> RecruitmentResponseDto.ContactDetails.builder().type(ct.getContactType()).label(ct.getLabel()).value(ct.getValue()).subValue(ct.getSubValue()).build()).collect(Collectors.toList()))
                        .interviewSlots(slotRepository.findAllByRecruitment_RecruitmentIdAndIsActiveTrue(recruitmentId).stream().map(s -> RecruitmentResponseDto.SlotDetails.builder().slotId(s.getSlotId()).date(s.getInterviewDate().toString()).time(s.getStartTime() != null ? s.getStartTime().toString() : "시간 미정").build()).collect(Collectors.toList()))
                        .build())
                .build();
    }

    /**
     * 3. 수정 및 삭제
     */
    @Transactional
    public RecruitmentResponseDto update(Integer id, RecruitmentRequestDto dto) {
        Recruitment r = recruitmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("공고 없음"));
        r.setTitle(dto.getTitle());
        r.setDescription(dto.getDescription());
        r.setApplyStartDate(dto.getApplyStartDate());
        r.setApplyEndDate(dto.getApplyEndDate());
        return RecruitmentResponseDto.builder().recruitmentId(r.getRecruitmentId()).title(r.getTitle()).build();
    }

    @Transactional
    public void delete(Integer id) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다. ID: " + id));

        // CascadeType.ALL 설정으로 인해 자식 데이터는 자동으로 삭제됩니다.
        recruitmentRepository.delete(recruitment);
        log.info(">>>> [삭제 완료] ID: {}", id);
    }

    /**
     * 4. 지원서 제출 및 메일 알림
     */
    @Transactional
    public void submitApplication(ApplicationRequestDto dto) {
        Recruitment recruit = recruitmentRepository.findById(dto.getRecruitmentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 공고가 존재하지 않습니다."));

        RecruitmentApplication application = RecruitmentApplication.builder()
                .recruitment(recruit) // ID 대신 객체 매핑
                .userName(dto.getUserName())
                .userMajor(dto.getUserMajor())
                .studentNumber(dto.getStudentNumber())
                .userPhone(dto.getUserPhone())
                .grade(dto.getGrade())
                .userEmail(dto.getUserEmail())
                .portfolioUrl(dto.getPortfolioUrl())
                .termsAgreed(dto.getTermsAgreed())
                .status("PENDING")
                .build();

        applicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public void sendEmailToSubscribers(String recruitmentTitle) {
        // 알림 서비스 로직 (기존 로직 유지)
    }
}