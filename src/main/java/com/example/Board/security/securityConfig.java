package com.example.Board.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 설정 클래스임을 선언
public class securityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 모든 요청 허용
                )
                .formLogin(login -> login
                        .loginPage("/login")  // 로그인 페이지 지정
                        .loginProcessingUrl("/auth/login")  // 로그인 요청 URL
                        .defaultSuccessUrl("/board/", true) // 로그인 성공 후 이동할 페이지
                        .permitAll()
                );
        return http.build();
    }

    @Bean // PasswordEncoder를 스프링 컨테이너에 빈으로 등록
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
