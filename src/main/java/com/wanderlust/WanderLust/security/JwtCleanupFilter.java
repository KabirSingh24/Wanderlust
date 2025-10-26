package com.wanderlust.WanderLust.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtCleanupFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtCleanupFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            String token = (String) session.getAttribute("JWT_TOKEN");

            if (token != null && jwtService.isTokenExpired(token)) {
                // Clear old JWT + session
                session.invalidate();
                Cookie jwtCookie = new Cookie("wanderlust", "");
                jwtCookie.setHttpOnly(true);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(0);
                response.addCookie(jwtCookie);

                Cookie sessionCookie = new Cookie("wanderlust_cookie", "");
                sessionCookie.setHttpOnly(true);
                sessionCookie.setPath("/");
                sessionCookie.setMaxAge(0);
                response.addCookie(sessionCookie);
            }
        }

        filterChain.doFilter(request, response);
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//    }
}
