package com.example.Board.repository;

import com.example.Board.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comments, Long> {
    // 특정 게시글의 댓글을 작성일 기준 오름차순으로 가져오기
    List<Comments> findByBoard_IdOrderByCreatedDateAsc(Long boardId);
}
