package com.example.Board.controller;

import com.example.Board.entity.Board;
import com.example.Board.entity.Comments;
import com.example.Board.entity.User;
import com.example.Board.repository.BoardRepository;
import com.example.Board.service.BoardService;
import com.example.Board.service.CommentService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final  CommentService commentService;
    private final BoardRepository boardRepository;

    @GetMapping
    public String boardList(Model model,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "10") int size,
                            HttpSession session) {

        // 로그인한 사용자 정보 가져오기
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("welcomeMessage", loggedInUser.getNickname() + "님 환영합니다!");
        }

        // 최신순 정렬 (ID 기준 내림차순)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Board> boardPage = boardRepository.findAll(pageable);

        model.addAttribute("boardPage", boardPage);
        return "boardlist";
    }


    // 수정 페이지로 이동 (GET 요청)
    @GetMapping("/editPage/{id}")
    public String boardEditPage(@PathVariable("id") Long id, Model model, HttpSession session){
        // 로그인한 사용자 확인
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login?error=로그인이 필요합니다.";
        }

        // 게시글 조회
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 작성자가 아니라면 접근 불가
        if (!board.getUser().getId().equals(loggedInUser.getId())) {
            return "redirect:/board/" + id + "?error=수정 권한이 없습니다.";
        }

        model.addAttribute("board", board);
        return "boardedit"; // 수정할 게시글을 모델에 담아서 페이지로 전달
    }


    // 글쓰기 페이지로 이동
    @GetMapping("/writePage")
    public String boardWritePage() {
        return "boardwrite";
    }

    //글쓰기 버튼을 눌렀을 때
    @PostMapping("/write")
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

        return "redirect:/board";
    }

    // 게시판 글 상세보기
    @GetMapping("/{id}")
    @Transactional
    public String boardDetail(@PathVariable("id") Long id, Model model) {
        // 조회수 증가
        boardRepository.incrementViewCount(id);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        List<Comments> comments = commentService.getCommentsByBoard(id); // 댓글 목록 조회
        model.addAttribute("board", board);
        model.addAttribute("comments", comments);

        return "boarddetail";
    }

    // 게시글 삭제
    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable("id") Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        // 작성자가 맞는지 확인
        if (loggedInUser != null && loggedInUser.getId().equals(board.getUser().getId())) {
            boardRepository.delete(board);
        }
        return "redirect:/board/";
    }

    // 게시글 검색
    @GetMapping("/search")
    public String search(@RequestParam("searchType") String searchType,
                         @RequestParam("query") String query,
                         @RequestParam(name = "page", defaultValue = "0") int page,
                         @RequestParam(name = "size", defaultValue = "10") int size,
                         Model model, HttpSession session) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Board> boardPage;

        if ("title".equalsIgnoreCase(searchType)) {
            boardPage = boardRepository.findByTitleContainingIgnoreCase(query, pageable);
        } else if ("writer".equalsIgnoreCase(searchType)) {
            boardPage = boardRepository.findByWriterContainingIgnoreCase(query, pageable);
        } else {
            boardPage = Page.empty();
        }

        model.addAttribute("boardPage", boardPage);
        model.addAttribute("searchType", searchType);
        model.addAttribute("query", query);

        return "boardlist";
    }

    @PutMapping("/{id}/edit")
    public String updateBoard(
            @PathVariable("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session
    ) {
        // 로그인된 사용자 확인
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login?error=로그인이 필요합니다.";
        }

        // 게시글 조회 및 작성자 확인
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!board.getUser().getId().equals(loggedInUser.getId())) {
            return "redirect:/board/" + id + "?error=수정 권한이 없습니다.";
        }

        // 게시글 수정
        board.setTitle(title);
        board.setContent(content);
        boardRepository.save(board);

        return "redirect:/board/" + id;  // 수정 후 해당 게시글 상세 페이지로 이동
    }

}
