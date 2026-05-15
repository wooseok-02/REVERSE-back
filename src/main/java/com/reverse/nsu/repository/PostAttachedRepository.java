package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Post;
import com.reverse.nsu.entity.PostAttached;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostAttachedRepository extends JpaRepository<PostAttached, Integer> {
    // 객체(Post)를 직접 넘겨서 조회/삭제하도록 수정
    List<PostAttached> findAllByPost(Post post);
    void deleteAllByPost(Post post);
}