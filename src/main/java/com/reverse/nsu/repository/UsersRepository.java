package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    @Query("SELECT u FROM Users u JOIN Officer o ON u.userId = o.updatedBy " +
            "WHERE u.userId = :userId AND o.email = :email")
    Optional<Users> findByUserIdAndOfficerEmail(@Param("userId") String userId, @Param("email") String email);
}