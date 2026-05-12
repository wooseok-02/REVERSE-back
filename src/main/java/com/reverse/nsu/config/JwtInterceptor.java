package com.reverse.nsu.config;

import com.reverse.nsu.service.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 요청은 CORS preflight이므로 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(401);
            response.getWriter().write("{\"success\":false,\"code\":\"UNAUTHORIZED\",\"message\":\"토큰이 없습니다.\"}");
            return false;
        }

        String token = header.substring(7);
        if (!jwtProvider.isValid(token)) {
            response.setStatus(401);
            response.getWriter().write("{\"success\":false,\"code\":\"UNAUTHORIZED\",\"message\":\"유효하지 않은 토큰입니다.\"}");
            return false;
        }

        // 토큰에서 userId 추출 후 request에 저장
        request.setAttribute("userId", jwtProvider.getUserId(token));
        return true;
    }
}