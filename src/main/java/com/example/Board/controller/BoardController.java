package com.example.Board.controller;

import com.example.Board.entity.Board;
import com.example.Board.entity.User;
import com.example.Board.repository.BoardRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;

    @GetMapping("/")
    public String boardList(Model model, HttpSession session) {
        // 로그인한 사용자 정보 가져오기
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("welcomeMessage", loggedInUser.getNickname() + "님 환영합니다!");
        }

        // DB에서 게시글 목록 조회
        List<Board> posts = boardRepository.findAll();
        model.addAttribute("posts", posts);

        return "boardlist";
    }

    @GetMapping("/writePage") // 글쓰기 페이지로 이동
    public String boardWritePage() {
        return "boardwrite";
    }

    @PostMapping("/write") //글쓰기 버튼을 눌렀을 때
    public String boardWrite(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session
    )  {
        // 세션에서 로그인한 사용자 정보를 가져옴
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login?error=로그인 후 이용해주세요.";
        }

        // 새로운 Board 객체 생성 및 값 설정
        Board board = new Board();
        board.setTitle(title);
        board.setContent(content);
        // 작성자(writer)는 로그인한 사용자의 닉네임 또는 username을 사용
        board.setWriter(loggedInUser.getNickname());
        board.setUser(loggedInUser);
        board.setView(0L); // 조회수 초기값 설정

        boardRepository.save(board);

        return "redirect:/board/";
    }

}
