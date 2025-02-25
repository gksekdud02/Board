package com.example.Board.controller;

import com.example.Board.entity.Comments;
import com.example.Board.entity.User;
import com.example.Board.service.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 특정 게시글의 댓글 목록 조회
    @GetMapping("/board/{boardId}")
    public String getCommentsByBoard(@PathVariable Long boardId, Model model) {
        List<Comments> comments = commentService.getCommentsByBoard(boardId);
        model.addAttribute("comments", comments);
        return "boarddetail"; // 게시글 상세보기 페이지로 이동
    }

    // 댓글 작성
    @PostMapping("/board/{boardId}/add")
    public String createComment(
            @PathVariable("boardId") Long boardId,
            @RequestParam("comment") String commentText,
            HttpSession session
    ) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login?error=로그인이 필요합니다.";
        }
        commentService.createComment(boardId, loggedInUser, commentText);
        return "redirect:/board/" + boardId; // 게시글 상세보기로 리다이렉트
    }

    // 댓글 삭제
    @PostMapping("/{boardId}/{commentId}/delete")
    public String deleteComment(
            @PathVariable("commentId") Long commentId,
            @PathVariable("boardId") Long boardId,
            HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login?error=로그인이 필요합니다.";
        }
        commentService.deleteComment(commentId, loggedInUser);
        return "redirect:/board/" + boardId;
    }
}
