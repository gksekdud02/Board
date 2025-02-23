package com.example.Board.service;

import com.example.Board.entity.Board;
import com.example.Board.repository.BoardRepository;
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

}
