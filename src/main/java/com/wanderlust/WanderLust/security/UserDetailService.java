package com.wanderlust.WanderLust.security;


import com.wanderlust.WanderLust.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    @Cacheable("user")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.wanderlust.WanderLust.entity.UserEntity user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        // Return Spring's built-in UserDetails with empty authorities
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(new ArrayList<>()) // no roles or authorities
                .build();
    }
}
