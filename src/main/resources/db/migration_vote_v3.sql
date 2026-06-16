-- VOTE_RECORD 테이블 유니크 제약 변경: (voteId, userId) → (voteId, userId, optionId)
-- 다중선택(isMultiple=true) 투표 지원을 위해 optionId 포함으로 변경
-- 실행 전 백업 권장

-- 1. 새 유니크 제약 먼저 추가 (voteId FK 인덱스 역할도 함께 수행)
ALTER TABLE VOTE_RECORD
    ADD CONSTRAINT uq_vote_user_option UNIQUE (voteId, userId, optionId);

-- 2. 기존 제약 삭제 (이제 voteId 인덱스가 위에서 확보됨)
ALTER TABLE VOTE_RECORD
    DROP INDEX uq_vote_user;
