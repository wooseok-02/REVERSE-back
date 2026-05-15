package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Comment;
import com.reverse.nsu.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // [수정] postId(Integer) 필드가 사라졌으므로, post(객체) 필드를 참조하도록 수정
    List<Comment> findAllByPostOrderByCreatedDateAsc(Post post);

    // [수정] 위와 동일하게 post 객체를 기준으로 카운트
    int countByPost(Post post);
}