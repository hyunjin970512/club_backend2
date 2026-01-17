package kr.co.koreazinc.app.model.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private static final String SECRET = "dev-secret-key-change-this-very-long";
    private static final long EXPIRE_MS = 1000 * 60 * 60 * 2; // 2시간

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String createToken(String empNo, String role) {
        return Jwts.builder()
                .setSubject(empNo)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
