package com.reverse.nsu.repository;

import com.reverse.nsu.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findTopByEmailAndIsVerifiedFalseOrderByCreatedDateDesc(String email);
    boolean existsByEmailAndIsVerifiedTrue(String email);
    List<EmailVerification> findAllByEmail(String email);
}
