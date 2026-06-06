package com.reverse.nsu.repository;

import com.reverse.nsu.entity.UserConsent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConsentRepository extends JpaRepository<UserConsent, Integer> {

    void deleteAllByUserId(String userId);
}
