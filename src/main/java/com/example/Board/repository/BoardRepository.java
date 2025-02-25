package com.example.Board.repository;

import com.example.Board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
     // 모든 게시글을 생성일 내림차순으로 조회
    List<Board> findAllByOrderByCreatedDateDesc();

    // 제목에 특정 키워드가 포함된 게시글을 검색 (대소문자 구분 없이)
    List<Board> findByTitleContainingIgnoreCase(String title);

    // 작성자(Writer)에 특정 키워드가 포함된 게시글을 검색 (대소문자 구분 없이)
    List<Board> findByWriterContainingIgnoreCase(String writer);

    // 특정 사용자가 작성한 게시글 조회 (User 엔티티와 연관된 user의 id로 조회)
    List<Board> findAllByUser_Id(Long userId);

    // 게시글을 생성일 기준으로 내림차순 정렬하여 페이징
    Page<Board> findAllByOrderByCreatedDateDesc(Pageable pageable);
    Page<Board> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Board> findByWriterContainingIgnoreCase(String writer, Pageable pageable);

    // 게시글의 조회수를 1 증가시키는 커스텀 쿼리 (서비스 계층에서 트랜잭션 관리 필요)
    @Modifying
    @Query("UPDATE Board b SET b.view = b.view + 1 WHERE b.id = :boardId")
    void incrementViewCount(@Param("boardId") Long boardId);

}
