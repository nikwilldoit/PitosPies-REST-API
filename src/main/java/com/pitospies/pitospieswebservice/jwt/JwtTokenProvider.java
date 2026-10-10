package com.pitospies.pitospieswebservice.jwt;

import com.pitospies.pitospieswebservice.services.AuthService;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Autowired
    private AuthService authService;

    @Value("${app.jwtSecret}") //inject secret key from application properties
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}") //inject the token expiration time from application properties
    private int jwtExpirationInMs;

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        Key key = KeyUtilController.base64ToSecretKey(jwtSecret, "HmacSHA512");

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();

        return token;
    }

    public boolean validateToken(String token) {
        try {
            Key key = KeyUtilController.base64ToSecretKey(jwtSecret, "HmacSHA512");

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            System.out.println(ex);
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Key key = KeyUtilController.base64ToSecretKey(jwtSecret, "HmacSHA512");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Extract the token without "Bearer " prefix
        }
        return null; // No token found
    }


    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);

        UserDetails userDetails = authService.loadUserByUsername(username);

        if (userDetails != null) {
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }

        return null;
    }

}