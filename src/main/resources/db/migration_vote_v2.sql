-- VOTE 테이블에 비밀투표, 참가 권한, 결과 조회 권한 컬럼 추가
-- 실행 전 백업 권장

ALTER TABLE VOTE
    ADD COLUMN isSecret       TINYINT(1) NOT NULL DEFAULT 0    COMMENT '비밀투표 여부 (0=공개, 1=비밀)',
    ADD COLUMN participantRole INT        NOT NULL DEFAULT 3    COMMENT '투표 참가 가능 최소 roleId (1=최고관리자~5=게스트). 기본 3=정회원',
    ADD COLUMN resultViewRole  INT        NOT NULL DEFAULT 3    COMMENT '공개투표 결과 조회 가능 최소 roleId. 비밀투표일 때는 무시됨. 기본 3=정회원';
