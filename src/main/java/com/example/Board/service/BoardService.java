package com.example.Board.service;

import com.example.Board.entity.Board;
import com.example.Board.entity.User;
import com.example.Board.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    // 모든 게시글 조회
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    // 특정 사용자가 작성한 게시글 조회 (BoardRepository에 해당 메서드가 있어야 함)
    public List<Board> findAllByUserId(Long userId) {
        return boardRepository.findAllByUser_Id(userId);
    }

    // 게시글 저장 (작성, 수정 등)
    public Board save(Board board) {
        return boardRepository.save(board);
    }

    // 페이징을 위한 메서드 추가
    public Page<Board> getBoardList(Pageable pageable) {
        return boardRepository.findAllByOrderByCreatedDateDesc(pageable);
    }

    @Transactional
    public void updateBoard(Long boardId, User loggedInUser, String title, String content) {
        // 수정할 게시글이 존재하는지 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 현재 로그인한 사용자가 작성자인지 검증
        if (!board.getUser().getId().equals(loggedInUser.getId())) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }

        // 게시글 수정
        board.setTitle(title);
        board.setContent(content);
    }
}
