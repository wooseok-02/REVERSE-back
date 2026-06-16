package com.reverse.nsu.repository;

import com.reverse.nsu.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findByRefreshToken(String refreshToken);

    Optional<UserToken> findByRefreshTokenAndIsRevoked(String refreshToken, Boolean isRevoked);

    void deleteAllByUserId(String userId);
}
