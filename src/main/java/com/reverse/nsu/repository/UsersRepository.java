package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // 🔥 임포트 추가 필요
import org.springframework.data.repository.query.Param; // 🔥 임포트 추가 필요
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    Optional<Users> findByUserIdAndUserEmail(String userId, String userEmail);
    Optional<Users> findByUserNameAndUserEmail(String userName, String userEmail);
    Optional<Users> findByUserEmail(String userEmail);


    @Query("SELECT u.role FROM Users u WHERE u.userId = :userId")
    Optional<Integer> findRoleIdByUserId(@Param("userId") String userId);
}