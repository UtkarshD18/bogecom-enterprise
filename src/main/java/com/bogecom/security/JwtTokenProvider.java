package com.bogecom.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final SecretKey key;
  private final long accessTokenExpirationMs;
  private final long refreshTokenExpirationMs;

  public JwtTokenProvider(
      @Value("${app.security.jwt.secret}") String secret,
      @Value("${app.security.jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
      @Value("${app.security.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTokenExpirationMs = accessTokenExpirationMs;
    this.refreshTokenExpirationMs = refreshTokenExpirationMs;
  }

  public String generateAccessToken(Long userId, String role) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);

    return Jwts.builder()
        .subject(Long.toString(userId))
        .claim("role", role)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(key)
        .compact();
  }

  public String generateRefreshToken(String tokenId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

    return Jwts.builder()
        .subject(tokenId)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(key)
        .compact();
  }

  public Long getUserIdFromJWT(String token) {
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

    return Long.parseLong(claims.getSubject());
  }

  public String getTokenIdFromJWT(String token) {
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

    return claims.getSubject();
  }

  public boolean validateToken(String authToken) {
    try {
      Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
      return true;
    } catch (Exception ex) {
      // Log exception details in a real scenario
      return false;
    }
  }
}
