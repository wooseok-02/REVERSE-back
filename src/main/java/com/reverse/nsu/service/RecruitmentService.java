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
     * 1. 신규 공고 생성 및 구독자 알림 발송
     */
    @Transactional
    public RecruitmentResponseDto save(RecruitmentRequestDto dto, String adminId) {
        Recruitment recruitment = dto.toEntity();
        recruitment.setUpdatedBy(adminId);
        recruitment.setIsActive(true);

        Recruitment savedRecruit = recruitmentRepository.save(recruitment);
        sendEmailToSubscribers(savedRecruit.getTitle());

        return RecruitmentResponseDto.builder()
                .recruitmentId(savedRecruit.getId())
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
                .map(r -> RecruitmentResponseDto.builder().recruitmentId(r.getId()).title(r.getTitle()).build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecruitmentResponseDto getById(Integer id) {
        Recruitment r = recruitmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("공고 없음"));
        return RecruitmentResponseDto.builder().recruitmentId(r.getId()).title(r.getTitle()).build();
    }

    @Transactional(readOnly = true)
    public RecruitmentResponseDto getRecruitPage(Integer recruitmentId) {
        Recruitment recruit = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다."));

        RecruitmentPage page = pageRepository.findByRecruitmentId(recruitmentId).orElse(null);

        if (page == null) {
            return RecruitmentResponseDto.builder().recruitmentId(recruit.getId()).title(recruit.getTitle()).build();
        }

        Integer pageId = page.getId();
        return RecruitmentResponseDto.builder()
                .recruitmentId(recruit.getId())
                .title(recruit.getTitle())
                .page(RecruitmentResponseDto.PageDetails.builder()
                        .year(page.getHeroYear()).title(page.getHeroTitle()).subtitle(page.getHeroSubTitle()).heroImageUrl(page.getHeroBgUrl())
                        .intros(introRepository.findAllByPageIdOrderBySortOrderAsc(pageId).stream().map(i -> RecruitmentResponseDto.IntroDetails.builder().contents(i.getContents()).sortOrder(i.getSortOrder()).build()).collect(Collectors.toList()))
                        .cards(cardRepository.findAllByPageIdOrderBySortOrderAsc(pageId).stream().map(c -> RecruitmentResponseDto.CardDetails.builder().applyField(c.getApplyField()).title(c.getCardTitle()).subTitle(c.getCardSubTitle()).desc(c.getCardDesc()).imageUrl(c.getImageUrl()).build()).collect(Collectors.toList()))
                        .galleries(galleryRepository.findAllByPageIdOrderBySortOrderAsc(pageId).stream().map(g -> RecruitmentResponseDto.GalleryDetails.builder().imageUrl(g.getImageUrl()).imageDesc(g.getImageDesc()).tag(g.getTag()).build()).collect(Collectors.toList()))
                        .contacts(contactRepository.findAllByPageIdOrderBySortOrderAsc(pageId).stream().map(ct -> RecruitmentResponseDto.ContactDetails.builder().type(ct.getContactType()).label(ct.getLabel()).value(ct.getValue()).subValue(ct.getSubValue()).build()).collect(Collectors.toList()))
                        .interviewSlots(slotRepository.findAllByRecruitmentIdAndIsActiveTrue(recruitmentId).stream().map(s -> RecruitmentResponseDto.SlotDetails.builder().slotId(s.getSlotId()).date(s.getInterviewDate().toString()).time("시간 미정").build()).collect(Collectors.toList()))
                        .build())
                .build();
    }

    /**
     * 3. 수정 및 삭제 (강력한 삭제 로직 포함)
     */
    @Transactional
    public RecruitmentResponseDto update(Integer id, RecruitmentRequestDto dto) {
        Recruitment r = recruitmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("공고 없음"));
        r.setTitle(dto.getTitle());
        r.setDescription(dto.getDescription());
        r.setApplyStartDate(dto.getApplyStartDate());
        r.setApplyEndDate(dto.getApplyEndDate());
        return RecruitmentResponseDto.builder().recruitmentId(r.getId()).title(r.getTitle()).build();
    }

    @Transactional
    public void delete(Integer id) {
        log.info(">>>> [완전 삭제 시작] ID: {}", id);
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다. ID: " + id));

        // 1. 자식 데이터들 먼저 삭제 (외래키 제약 방지)
        slotRepository.deleteByRecruitmentId(id);
        applicationRepository.deleteByRecruitmentId(id);

        // 2. 상세 페이지 삭제
        pageRepository.findByRecruitmentId(id).ifPresent(page -> {
            clearSubData(page.getId()); // 아래 정의된 헬퍼 메서드 호출
            pageRepository.delete(page);
        });

        // 3. 공고 본체 삭제
        recruitmentRepository.delete(recruitment);
        log.info(">>>> [삭제 완료] ID: {}", id);
    }

    // 컨트롤러에서 deletePage를 찾을 때를 대비한 별칭
    public void deletePage(Integer id) {
        this.delete(id);
    }

    // 헬퍼 메서드: 상세페이지 하위 데이터들 삭제
    private void clearSubData(Integer pageId) {
        introRepository.deleteByPageId(pageId);
        cardRepository.deleteByPageId(pageId);
        galleryRepository.deleteByPageId(pageId);
        contactRepository.deleteByPageId(pageId);
    }


    @Transactional
    public void createOrUpdatePage(Integer recruitId, RecruitmentResponseDto.PageDetails dto, String adminId) {

        RecruitmentPage page = pageRepository.findByRecruitmentId(recruitId)
                .orElseGet(() -> {
                    RecruitmentPage newPage = new RecruitmentPage();
                    newPage.setRecruitmentId(recruitId); // 새 객체일 때 확실히 세팅
                    return newPage;
                });

        page.setHeroYear(dto.getYear());      // DTO는 year, 엔티티는 heroYear
        page.setHeroTitle(dto.getTitle());    // DTO는 title, 엔티티는 heroTitle
        page.setHeroSubTitle(dto.getSubtitle()); // DTO는 subtitle, 엔티티는 heroSubTitle

        page.setHeroBgUrl(dto.getHeroImageUrl());
        page.setHeroBtnText(dto.getHeroBtnText());

        page.setUpdatedBy(adminId);
        page.setIsActive(true);

        page.setHeroBtnText(dto.getHeroBtnText());

        page.setUpdatedBy(adminId);
        page.setIsActive(true);

        RecruitmentPage savedPage = pageRepository.save(page);

        Integer pageId = savedPage.getId();
        clearSubData(pageId);

        if (dto.getIntros() != null) {
            introRepository.saveAll(dto.getIntros().stream().map(i -> RecruitmentPageIntro.builder().pageId(pageId).contents(i.getContents()).sortOrder(i.getSortOrder()).updatedBy(adminId).build()).collect(Collectors.toList()));
        }
        if (dto.getCards() != null) {
            cardRepository.saveAll(dto.getCards().stream().map(c -> RecruitmentPageFieldCard.builder().pageId(pageId).applyField(c.getApplyField()).cardTitle(c.getTitle()).cardSubTitle(c.getSubTitle()).cardDesc(c.getDesc()).imageUrl(c.getImageUrl()).sortOrder(0).updatedBy(adminId).build()).collect(Collectors.toList()));
        }
        if (dto.getGalleries() != null) {
            galleryRepository.saveAll(dto.getGalleries().stream().map(g -> RecruitmentPageGallery.builder().pageId(pageId).imageUrl(g.getImageUrl()).imageDesc(g.getImageDesc()).tag(g.getTag()).sortOrder(0).isVisible(true).updatedBy(adminId).build()).collect(Collectors.toList()));
        }
        if (dto.getContacts() != null) {
            contactRepository.saveAll(dto.getContacts().stream().map(ct -> RecruitmentPageContact.builder().pageId(pageId).contactType(ct.getType()).label(ct.getLabel()).value(ct.getValue()).subValue(ct.getSubValue()).sortOrder(0).updatedBy(adminId).build()).collect(Collectors.toList()));
        }
    }

    // 하위 데이터 정리를 위한 헬퍼 메서드 (이름 통일)
    /**
     * 4. 이메일 및 지원서
     */
    @Transactional(readOnly = true)
    public void sendEmailToSubscribers(String recruitmentTitle) {
        List<RecruitmentApplication> subscribers = applicationRepository.findAllByTermsAgreedTrue();
        if (subscribers.isEmpty()) return;
        Set<String> emailSet = subscribers.stream().map(RecruitmentApplication::getUserEmail).filter(e -> e != null && !e.isEmpty()).collect(Collectors.toSet());
        for (String email : emailSet) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("reverse.nsu@gmail.com");
            message.setTo(email);
            message.setSubject("[REVERSE] 신규 모집 공고 알림: " + recruitmentTitle);
            message.setText("안녕하세요, REVERSE입니다!\n새로운 모집 소식을 전해드립니다: " + recruitmentTitle);
            try { mailSender.send(message); } catch (Exception e) { log.error("메일 발송 실패: {}", e.getMessage()); }
        }
    }

    @Transactional
    public void submitApplication(ApplicationRequestDto dto) {
        log.info(">>>> [지원서 접수] 지원자: {}, 학과: {}", dto.getUserName(), dto.getUserMajor());

        RecruitmentApplication application = RecruitmentApplication.builder()
                .recruitmentId(dto.getRecruitmentId())
                .userName(dto.getUserName())
                .userMajor(dto.getUserMajor())
                .studentNumber(dto.getStudentNumber())
                .userPhone(dto.getUserPhone())
                .grade(dto.getGrade())
                .userEmail(dto.getUserEmail())
                .portfolioUrl(dto.getPortfolioUrl())
                .termsAgreed(dto.getTermsAgreed())
                .status("PENDING")
                .createdDate(LocalDateTime.now()) // 이미지 에러: LocalDateTime 임포트 필요
                .modifiedDate(LocalDateTime.now())
                .build();

        applicationRepository.save(application);
        log.info(">>>> [성공] 님의 지원서가 정상적으로 접수되었습니다.");
    }



}