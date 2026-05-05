package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    /**
     * [수정] 불필요한 Officer 조인을 제거하고 USERS 테이블의 컬럼을 직접 참조합니다.
     * DB의 userEmail 컬럼과 매칭됩니다.
     */
    Optional<Users> findByUserIdAndUserEmail(String userId, String userEmail);
}