-- 스터디 신청(승인 절차) 기능을 위한 신규 테이블
-- ddl-auto: none 이므로 수동으로 실행 필요

-- 1. 스터디 신청 (대기/승인/반려 상태 관리)
CREATE TABLE STUDY_APPLICATION (
    studyApplicationId INT AUTO_INCREMENT PRIMARY KEY,
    studyId INT NOT NULL,
    userId VARCHAR(15) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    processedBy VARCHAR(15) NULL,
    processedDate DATETIME NULL,
    appliedDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_STUDY_APPLICATION_STUDY FOREIGN KEY (studyId) REFERENCES STUDY(studyId) ON DELETE CASCADE,
    INDEX IDX_STUDY_APPLICATION_STUDY_USER_STATUS (studyId, userId, status)
);

-- 2. 스터디 신청 시 선택한 가능 요일/시간 (신청 1건당 1개 이상)
CREATE TABLE STUDY_APPLICATION_AVAILABILITY (
    studyApplicationAvailabilityId INT AUTO_INCREMENT PRIMARY KEY,
    studyApplicationId INT NOT NULL,
    dayOfWeek INT NOT NULL,
    availableTime TIME NOT NULL,
    CONSTRAINT FK_STUDY_APP_AVAIL_APPLICATION FOREIGN KEY (studyApplicationId) REFERENCES STUDY_APPLICATION(studyApplicationId) ON DELETE CASCADE,
    CONSTRAINT UQ_STUDY_APPLICATION_AVAILABILITY UNIQUE (studyApplicationId, dayOfWeek)
);
