package com.reverse.nsu.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * USERS 테이블 테스트 데이터 INSERT SQL 생성기
 * IntelliJ에서 main 메서드 옆 ▶ 버튼으로 실행
 */
public class TestDataSqlGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 테스트 계정 정의: {userId, userName, userEmail, plainPassword, roleId, userIntroduce, userMbti}
        // roleId: 1=SUPER_ADMIN, 2=ADMIN, 3=MEMBER, 4=ASSOCIATE, 5=GUEST
        Object[][] users = {
            {"superadmin", "최고관리자", "superadmin@test.com", "test1234!", 1, "최고 관리자 계정", "INTJ"},
            {"admin01",    "관리자",    "admin@test.com",      "test1234!", 2, "관리자 계정",    "ENTJ"},
            {"member01",   "현부원",    "member@test.com",     "test1234!", 3, "정회원 테스트",  "INFP"},
            {"associate01", "준회원",    "associate@test.com",      "test1234!", 4, "준회원 계정",    "ENTJ"},
            {"guest01",    "게스트",  "guest@test.com",      "test1234!", 5, "게스트 계정",   "ENFP"  },
        };

        System.out.println("-- ==========================================");
        System.out.println("-- USERS 테이블 테스트 데이터 (비밀번호: Test1234!)");
        System.out.println("-- ==========================================");

        for (Object[] u : users) {
            String userId      = (String)  u[0];
            String userName    = (String)  u[1];
            String userEmail   = (String)  u[2];
            String rawPassword = (String)  u[3];
            int    roleId      = (int)     u[4];
            String introduce   = (String)  u[5];
            String mbti        = (String)  u[6];

            String hash = encoder.encode(rawPassword);
            String introduceVal = introduce != null ? "'" + introduce + "'" : "NULL";
            String mbtiVal      = mbti      != null ? "'" + mbti      + "'" : "NULL";

            System.out.printf(
                "INSERT INTO USERS (userId, roleId, userName, userEmail, userPassword, userIntroduce, userMbti) VALUES ('%s', %d, '%s', '%s', '%s', %s, %s);%n",
                userId, roleId, userName, userEmail, hash, introduceVal, mbtiVal
            );
        }

        System.out.println();
        System.out.println("-- USER_CONSENT (위 INSERT 실행 후 실행)");
        System.out.println("-- consentItemId: 1=이용약관(필수), 2=개인정보(필수), 3=마케팅(선택), 4=이벤트(선택)");

        String[] userIds = {"superadmin", "admin01", "member01", "guest01"};
        for (String uid : userIds) {
            System.out.printf(
                "INSERT INTO USER_CONSENT (userId, consentItemId, isAgreed, agreedDate) VALUES ('%s', 1, 1, NOW()), ('%s', 2, 1, NOW()), ('%s', 3, 0, NULL), ('%s', 4, 0, NULL);%n",
                uid, uid, uid, uid
            );
        }
    }
}
