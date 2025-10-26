package com.wanderlust.WanderLust.security;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailService userDetailService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
        throws ServletException, IOException {

        try {
            String token = null;

            // 1 Try from cookie first
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("wanderlust".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            // 2 If not in cookie, try Authorization header
            if (token == null) {
                final String requestTokenHeader = request.getHeader("Authorization");
                if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                    token = requestTokenHeader.substring(7).trim();
                }
            }

            if (token != null && !jwtService.isTokenValid(token)) {
                // Token invalid → clear cookies/session and redirect
                clearCookies(request, response);
                response.sendRedirect("/listings/all");
                return;
            }

        // 3 If token found, validate and set authentication
            if (token != null) {
                String email = jwtService.getUsernameFromToken(token);
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailService.loadUserByUsername(email);
                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities()
                                );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }

        // 4 Continue filter chain
            filterChain.doFilter(request, response);

        } catch (Exception e) {
        handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

    private void clearCookies(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        Cookie jwtCookie = new Cookie("wanderlust", "");
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        Cookie sessionCookie = new Cookie("wanderlust_cookie", "");
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setMaxAge(0);
        response.addCookie(sessionCookie);
    }
}
