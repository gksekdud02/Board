package com.example.Board.controller;

import com.example.Board.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/board")
@Controller
public class BoardController {

    @GetMapping("/")
    public String boardList(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("welcomeMessage", loggedInUser.getNickname() + "님 환영합니다!");
        }
        // posts 등 다른 데이터도 model에 추가
        return "boardlist";
    }

    @GetMapping("/write") // 글쓰기 페이지로 이동
    public String boardWrite() {
        return "boardwrite";
    }
}
