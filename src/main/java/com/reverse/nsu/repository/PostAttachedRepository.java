package com.reverse.nsu.repository;

import com.reverse.nsu.entity.PostAttached;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostAttachedRepository extends JpaRepository<PostAttached, Integer> {

    // [수정] post 객체 내부의 postId를 참조하도록 변경
    List<PostAttached> findAllByPost_PostId(Integer postId);

    // [수정] 위와 동일하게 변경
    void deleteAllByPost_PostId(Integer postId);
}