package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    /**
     * 기본 제공 메서드:
     * - existsById(String userId): DB에 해당 아이디가 존재하는지 확인 (boolean 반환)
     * - findById(String userId): 아이디로 유저 정보 조회
     */

    // 아이디와 이메일로 사용자 찾기 (비밀번호 찾기 등 인증용)
    Optional<Users> findByUserIdAndUserEmail(String userId, String userEmail);

    // 이름과 이메일로 사용자 찾기 (아이디 찾기용)
    Optional<Users> findByUserNameAndUserEmail(String userName, String userEmail);

    // 이메일로 사용자 찾기 (중복 가입 방지용)
    Optional<Users> findByUserEmail(String userEmail);
}