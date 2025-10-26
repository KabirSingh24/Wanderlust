package com.wanderlust.WanderLust.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 🔒 Protected routes first
                        .requestMatchers(HttpMethod.GET,"/listings/new").authenticated()
                        .requestMatchers(HttpMethod.POST,"/listings/*/edit/update").authenticated()
                        .requestMatchers(HttpMethod.POST, "/listings/*/delete").authenticated()
                        .requestMatchers(HttpMethod.GET, "/listings/*/delete").authenticated()
                        .requestMatchers(HttpMethod.POST,"/reviews/add/*").authenticated()
                        .requestMatchers(HttpMethod.POST,"/reviews/*/delete/*").authenticated()

                        // 🆓 Public routes below
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/listings/**").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e.authenticationEntryPoint(jwtEntryPoint))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
