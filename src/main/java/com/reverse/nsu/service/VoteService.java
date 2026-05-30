package com.reverse.nsu.service;

import com.reverse.nsu.dto.VoteListResponseDto;
import com.reverse.nsu.dto.VoteRequestDto;
import com.reverse.nsu.dto.VoteResponseDto;
import com.reverse.nsu.entity.Vote;
import com.reverse.nsu.entity.VoteOption;
import com.reverse.nsu.entity.VoteRecord;
import com.reverse.nsu.repository.VoteOptionRepository;
import com.reverse.nsu.repository.VoteRecordRepository;
import com.reverse.nsu.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;

    @Transactional
    public Integer createVote(VoteRequestDto dto, String userId) {
        validateRequest(dto);

        Vote vote = Vote.create(dto, userId);
        voteRepository.save(vote);

        List<String> optionTexts = dto.getOptions();
        for (int i = 0; i < optionTexts.size(); i++) {
            VoteOption option = VoteOption.create(vote, optionTexts.get(i), i);
            voteOptionRepository.save(option);
        }

        return vote.getVoteId();
    }

    @Transactional(readOnly = true)
    public Page<VoteListResponseDto> getVoteList(Pageable pageable) {
        return voteRepository.findAllByOrderByCreatedDateDesc(pageable)
                .map(VoteListResponseDto::new);
    }

    @Transactional(readOnly = true)
    public VoteResponseDto getVote(Integer voteId, String userId) {
        Vote vote = findVoteOrThrow(voteId);
        Integer myVotedOptionId = null;
        if (userId != null) {
            myVotedOptionId = voteRecordRepository.findByVoteAndUserId(vote, userId)
                    .map(r -> r.getOption().getOptionId())
                    .orElse(null);
        }
        return new VoteResponseDto(vote, myVotedOptionId);
    }

    @Transactional
    public void castVote(Integer voteId, Integer optionId, String userId) {
        Vote vote = findVoteOrThrow(voteId);

        if (vote.isClosed()) {
            throw new IllegalStateException("마감된 투표입니다.");
        }
        if (voteRecordRepository.existsByVoteAndUserId(vote, userId)) {
            throw new IllegalStateException("이미 투표하셨습니다.");
        }

        VoteOption option = voteOptionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));

        if (!option.getVote().getVoteId().equals(voteId)) {
            throw new IllegalArgumentException("해당 투표의 항목이 아닙니다.");
        }

        VoteRecord record = VoteRecord.create(vote, option, userId);
        voteRecordRepository.save(record);
        option.incrementCount();
    }

    @Transactional
    public void cancelVote(Integer voteId, String userId) {
        Vote vote = findVoteOrThrow(voteId);

        if (vote.isClosed()) {
            throw new IllegalStateException("마감된 투표는 취소할 수 없습니다.");
        }

        VoteRecord record = voteRecordRepository.findByVoteAndUserId(vote, userId)
                .orElseThrow(() -> new IllegalStateException("투표 기록이 없습니다."));

        record.getOption().decrementCount();
        voteRecordRepository.delete(record);
    }

    @Transactional
    public void updateVote(Integer voteId, VoteRequestDto dto, String userId) {
        Vote vote = findVoteOrThrow(voteId);

        if (!vote.getUserId().equals(userId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }
        if (!vote.getRecords().isEmpty()) {
            throw new IllegalStateException("투표가 시작된 후에는 수정할 수 없습니다.");
        }

        validateRequest(dto);
        vote.update(dto);

        voteOptionRepository.deleteAllByVote(vote);

        List<String> optionTexts = dto.getOptions();
        for (int i = 0; i < optionTexts.size(); i++) {
            VoteOption option = VoteOption.create(vote, optionTexts.get(i), i);
            voteOptionRepository.save(option);
        }
    }

    @Transactional
    public void deleteVote(Integer voteId, String userId) {
        Vote vote = findVoteOrThrow(voteId);

        if (!vote.getUserId().equals(userId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        voteRepository.delete(vote);
    }

    private Vote findVoteOrThrow(Integer voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표입니다. ID: " + voteId));
    }

    private void validateRequest(VoteRequestDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
        if (dto.getOptions() == null || dto.getOptions().size() < 2) {
            throw new IllegalArgumentException("투표 항목은 최소 2개 이상 필요합니다.");
        }
    }
}
