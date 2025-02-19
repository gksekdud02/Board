package com.example.Board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User{ //회원정보 저장하는 User 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // 아이디

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false, unique = true)
    private String nickname; // 닉네임

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Column(nullable = false)
    private String createdDate; // 생성일

    @Column(nullable = false)
    private String modifiedDate; // 수정일

    @PrePersist
    public void prePersist() {
        String now = LocalDateTime.now().toString();
        this.createdDate = now;
        this.modifiedDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now().toString();
    }
}
