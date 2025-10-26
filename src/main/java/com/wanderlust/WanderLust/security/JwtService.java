package com.wanderlust.WanderLust.security;

import com.wanderlust.WanderLust.entity.UserEntity;
import com.wanderlust.WanderLust.repo.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtService {
    @Value("${jwt.secretkey}")
    private  String jwtSecretKey;
    private final UserRepo userRepo;

    public JwtService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    private SecretKey getSecretKey(){return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));}

    public String generateToken(UserEntity user){
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId",user.getId().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+100*60*60))
                .signWith(getSecretKey())
                .compact();
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims clamis= Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = clamis.getSubject();
            Date exp = clamis.getExpiration();

            return email.equals(userDetails.getUsername()) && !exp.before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }


    public UserEntity getUserFromToken(String token) {
        String email = getUsernameFromToken(token);
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for token"));
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true; // Treat parsing errors as expired
        }
    }

    private Date extractExpiration(String token) {
        Claims clamis= Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return clamis.getExpiration();
    }

}
