package com.wanderlust.WanderLust.security;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//        try{
//            final String requestTokenHeader=request.getHeader("Authorization");
//            String token=null;
//            if(requestTokenHeader==null || !requestTokenHeader.startsWith("Bearer")){
//                filterChain.doFilter(request,response);
//                return;
//            }else {
//                // Try from cookies
//                if (request.getCookies() != null) {
//                    for (Cookie cookie : request.getCookies()) {
//                        if (cookie.getName().equals("jwt")) {
//                            token = cookie.getValue();
//                            filterChain.doFilter(request,response);
//                        }
//                    }
//                }
//            }
//            token=requestTokenHeader.substring(7).trim();
//            String email=jwtService.getUsernameFromToken(token);
//
//            if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
//                UserDetails userDetails=userDetailService.loadUserByUsername(email);
//                if(jwtService.validateToken(token,userDetails)){
//                    UsernamePasswordAuthenticationToken authToken=
//                            new UsernamePasswordAuthenticationToken(
//                                    userDetails,null,userDetails.getAuthorities()
//                            );
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            }
//            filterChain.doFilter(request,response);
//        }catch(Exception e){
//            handlerExceptionResolver.resolveException(request,response,null,e);
////            response.sendRedirect("/api/v1/auth/signup");
//        }
//    }

@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
        throws ServletException, IOException {

    try {
        String token = null;

        // 1️⃣ Try from cookie first
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("wanderlust".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 2️⃣ If not in cookie, try Authorization header
        if (token == null) {
            final String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                token = requestTokenHeader.substring(7).trim();
            }
        }

        // 3️⃣ If token found, validate and set authentication
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

        // 4️⃣ Continue filter chain
        filterChain.doFilter(request, response);

    } catch (Exception e) {
        handlerExceptionResolver.resolveException(request, response, null, e);
    }
}





}
