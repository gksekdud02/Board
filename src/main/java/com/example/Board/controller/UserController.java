package com.example.Board.controller;

import com.example.Board.entity.User;
import com.example.Board.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/signup") // 회원가입 페이지로 이동
    public String signupPage() {
        return "signup"; // signup.html 반환
    }

    @GetMapping("/login") // 로그인 페이지로 이동
    public String loginPage() {
        return "login"; // login.html 반환
    }

    @PostMapping("/register") // 회원가입 처리
    public String register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("nickname") String nickname,
            @RequestParam("email") String email
    ) {
        // 비밀번호와 확인 비밀번호가 일치하는지 검증
        if (!password.equals(confirmPassword)){
            // 일치하지 않으면 회원가입 페이지로 다시 리다이렉트하거나 에러메세지 표시
            return "redirect:/auth/signup?error=비밀번호가 일치하지 않습니다.";
        }
        userService.registerUser(username, password, nickname, email);
        return "redirect:/board/";
    }

    @PostMapping("/signin")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session
    ) {
        try {
            User user = userService.findUserByUsername(username);
            if (user == null) {
                String errorMessage = URLEncoder.encode("없는 아이디 입니다.", "UTF-8");
                return "redirect:/auth/login?error=" + errorMessage;
            }
            if (!userService.matchesPassword(password, user.getPassword())) {
                String errorMessage = URLEncoder.encode("아이디와 비밀번호가 일치하지 않습니다.", "UTF-8");
                return "redirect:/auth/login?error=" + errorMessage;
            }
            session.setAttribute("loggedInUser", user);
            return "redirect:/board/";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "redirect:/auth/login?error=내부+오류";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/board/"; // 로그아웃 후 메인페이지로 리다이렉트
    }

    // 닉네임 중복 확인
    @GetMapping("/checkNickname")
    @ResponseBody
    public Map<String, Boolean> checkNickname(@RequestParam("nickname") String nickname) {
        boolean available = userService.isNicknameAvailable(nickname);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", available);
        return response;
    }


}
