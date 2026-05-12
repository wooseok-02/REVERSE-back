package com.reverse.nsu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 1. 기본적으로 인증이 필요한 경로들
                .addPathPatterns("/api/posts/**", "/api/user/**")

                // 2. [수정] 인증 없이 누구나 접근 가능한(Public) 경로들만 제외
                .excludePathPatterns(
                        "/api/auth/**",            // 로그인, 회원가입
                        "/api/posts/notices",      // 공지사항 목록 조회
                        "/api/posts/list/**",      // 일반 게시글 목록 조회
                        "/api/posts/detail/**"     // 게시글 상세 조회
                );

        // 기존의 .excludePathPatterns("/api/posts/**") 는 삭제했습니다.
        // 이제 공지사항 등록, 수정, 삭제는 인터셉터에 걸려서 토큰 검사를 받게 됩니다!
    }
}