package com.example.Board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comments {

    @Id //기본키 
    @GeneratedValue(strategy = GenerationType.IDENTITY) //자동증가
    private Long id;

    @Column(nullable = false)
    private String comment;

    @Column(name = "created_date", nullable = false)
    private String createdDate;

    @Column(name = "modified_date", nullable = false)
    private String modifiedDate;

    // 외래키: 게시글 번호 (Board 엔티티와 다대일 관계)
    @ManyToOne(optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // 외래키: 계정 번호 (User 엔티티와 다대일 관계)
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        String now = java.time.LocalDateTime.now().toString();
        this.createdDate = now;
        this.modifiedDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = java.time.LocalDateTime.now().toString();
    }
}