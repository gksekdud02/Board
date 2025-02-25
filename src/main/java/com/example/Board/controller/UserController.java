package com.example.Board.controller;

import com.example.Board.entity.Board;
import com.example.Board.entity.User;
import com.example.Board.service.BoardService;
import com.example.Board.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BoardService boardService;

    @GetMapping("/signup") // 회원가입 페이지로 이동
    public String signupPage() {
        return "signup"; // signup.html 반환
    }

    @GetMapping("/login") // 로그인 페이지로 이동
    public String loginPage()  {
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
        return "redirect:/board";
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
            return "redirect:/board";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "redirect:/auth/login?error=내부+오류";
        }
    }

    @GetMapping("/logout") //로그아웃
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/board"; // 로그아웃 후 메인페이지로 리다이렉트
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

    // 내 정보 페이지: 사용자가 자신의 정보를 확인하고, 작성한 게시글, 탈퇴, 닉네임/비밀번호 변경 기능을 사용할 수 있음.
    @GetMapping("/info/{id}")
    public String info(@PathVariable("id") Long id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        // 로그인하지 않았거나, 요청한 id와 세션의 id가 다르면 로그인 페이지로 리다이렉트
        if (loggedInUser == null || !loggedInUser.getId().equals(id)) {
            return "redirect:/auth/login?error=" + URLEncoder.encode("로그인이 필요합니다.", StandardCharsets.UTF_8);
        }
        model.addAttribute("user", loggedInUser);
        // 해당 사용자가 작성한 게시글 목록 조회
        List<Board> posts = boardService.findAllByUserId(id);
        model.addAttribute("posts", posts);
        return "info"; // info.html 템플릿 반환
    }

    // 회원 탈퇴
    @PostMapping("/withdraw")
    public String withdraw(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/auth/login?error=" + URLEncoder.encode("로그인이 필요합니다.", StandardCharsets.UTF_8);
        }
        userService.withdraw(user.getId());
        session.invalidate();
        return "redirect:/board"; // 탈퇴 후 메인 페이지로 이동
    }

    // 닉네임 변경
    @PostMapping("/changeNickname")
    public String changeNickname(@RequestParam("newNickname") String newNickname, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/auth/login?error=" + URLEncoder.encode("로그인이 필요합니다.", StandardCharsets.UTF_8);
        }
        try {
            userService.changeNickname(user.getId(), newNickname);
            // 세션 업데이트
            user.setNickname(newNickname);
            session.setAttribute("loggedInUser", user);
            return "redirect:/auth/info/" + user.getId();
        } catch (Exception e) {
            String errorMsg = "닉네임 변경 실패";
            return "redirect:/auth/info/" + user.getId() + "?error=" + URLEncoder.encode(errorMsg, StandardCharsets.UTF_8);
        }
    }

    // 비밀번호 변경
    @PostMapping("/changePassword")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/auth/login?error=" + URLEncoder.encode("로그인이 필요합니다.", StandardCharsets.UTF_8);
        }
        try {
            userService.changePassword(user.getId(), currentPassword, newPassword);
            return "redirect:/auth/info/" + user.getId();
        } catch (Exception e) {
            String errorMsg = "비밀번호 변경 실패";
            return "redirect:/auth/info/" + user.getId() + "?error=" + URLEncoder.encode(errorMsg, StandardCharsets.UTF_8);
        }
    }


}
