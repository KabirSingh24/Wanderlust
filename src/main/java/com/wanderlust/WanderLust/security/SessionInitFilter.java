package com.wanderlust.WanderLust.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SessionInitFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Ensure a session exists for every request
        HttpSession session = request.getSession(true); // creates session if not exists

        // You can optionally log session creation
        if (session.isNew()) {
            System.out.println("✅ New session created: " + session.getId());
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}