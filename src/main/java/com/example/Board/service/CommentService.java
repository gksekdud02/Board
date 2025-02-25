package com.example.Board.service;

import com.example.Board.entity.Board;
import com.example.Board.entity.Comments;
import com.example.Board.entity.User;
import com.example.Board.repository.BoardRepository;
import com.example.Board.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    //특정 게시글의 댓글 목록 조회
    public List<Comments> getCommentsByBoard(Long boardId) {
        return commentRepository.findByBoard_IdOrderByCreatedDateAsc(boardId);
    }

    // 댓글 작성
    @Transactional
    public void createComment(Long boardId, User user, String commentText) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        Comments comment = new Comments();
        comment.setBoard(board);
        comment.setUser(user);
        comment.setComment(commentText);
        commentRepository.save(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        // 댓글 작성자만 삭제 가능
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }
}
