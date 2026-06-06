package com.reverse.nsu.repository;

import com.reverse.nsu.entity.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Integer> {
    // 💡 유저 고유 ID를 기반으로 프로필 사진 정보를 긁어오기 위한 핵심 쿼리 메소드
    Optional<UserPhoto> findByUserId(String userId);

    void deleteAllByUserId(String userId);
}