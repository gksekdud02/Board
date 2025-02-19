package com.example.Board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "board")
@Getter
@Setter
@NoArgsConstructor
public class Board {

    @Id //기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY) //자동증가
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private Long view;

    @Column(name = "created_date", nullable = false)
    private String createdDate;

    @Column(name = "modified_date", nullable = false)
    private String modifiedDate;

    // 외래키: 계정번호 (User 엔티티와 다대일 관계)
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 엔티티 저장 전 자동으로 생성일과 수정일을 설정
    @PrePersist
    public void prePersist() {
        String now = java.time.LocalDateTime.now().toString();
        this.createdDate = now;
        this.modifiedDate = now;
        if (this.view == null) {
            this.view = 0L;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = java.time.LocalDateTime.now().toString();
    }
}
