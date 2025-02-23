package com.example.Board.service;

import com.example.Board.entity.User;
import com.example.Board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(String username, String password, String nickname, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // 비밀번호 암호화
        user.setNickname(nickname);
        user.setEmail(email);

        userRepository.save(user);
    }

    // 로그인 시 아이디와 비밀번호를 검증하는 메서드
    public User authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    // DB에서 username으로 User 객체를 검색
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // 원시 비밀번호와 암호화된 비밀번호 비교
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // 닉네임 중복 여부 확인 메서드
    public boolean isNicknameAvailable(String nickname) {
        return userRepository.findByNickname(nickname).isEmpty();
    }

    // 회원 탈퇴: userId로 삭제
    public void withdraw(Long userId) {
        userRepository.deleteById(userId);
    }

    // 닉네임 변경
    public void changeNickname(Long userId, String newNickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (userRepository.findByNickname(newNickname).isPresent()) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }
        user.setNickname(newNickname);
        userRepository.save(user);
    }

    // 비밀번호 변경: 현재 비밀번호와 비교 후 새 비밀번호 저장
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalStateException("현재 비밀번호가 일치하지 않습니다.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
