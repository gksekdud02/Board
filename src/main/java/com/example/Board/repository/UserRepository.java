package com.example.Board.repository;

import com.example.Board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 특정 username을 가진 사용자가 있는지 확인
    Optional<User> findByUsername(String username);

    // 특정 nickname을 가진 사용자가 있는지 확인
    Optional<User> findByNickname(String nickname);

    // 특정 email을 가진 사용자가 있는지 확인
    Optional<User> findByEmail(String email);
}
