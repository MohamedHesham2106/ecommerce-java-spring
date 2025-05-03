package com.mohamedheshsam.main.security.jwt;

import com.mohamedheshsam.main.security.user.ShopUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

  private final JwtProperties props;
  private Key signingKey;

  public JwtUtils(JwtProperties props) {
    this.props = props;
  }

  @PostConstruct
  private void init() {
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(props.getJwtSecret()));
    System.out.println("[JwtUtils] Token expiration duration: " + props.getExpiration());
  }

  public String generateTokenForUser(Authentication authentication) {
    ShopUserDetails user = (ShopUserDetails) authentication.getPrincipal();
    long now = System.currentTimeMillis();
    long expMillis = props.getExpiration().toMillis();
    Date issuedAt = new Date(now);
    Date expiration = new Date(now + expMillis);
    System.out.println("[JwtUtils] Issued at: " + issuedAt + ", Expires at: " + expiration);

    return Jwts.builder()
        .setSubject(user.getEmail())
        .claim("id", user.getId())
        .claim("role", user.getRole().name())
        .setIssuedAt(issuedAt)
        .setExpiration(expiration)
        .signWith(signingKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public String getUsernameFromToken(String token) {
    try {
      return parseClaims(token).getSubject();
    } catch (ExpiredJwtException e) {
      throw new JwtException("Token expired at " + e.getClaims().getExpiration());
    } catch (JwtException e) {
      throw new JwtException("Invalid JWT: " + e.getMessage());
    }
  }

  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      throw new JwtException("Token expired at " + e.getClaims().getExpiration());
    } catch (JwtException e) {
      throw new JwtException("Invalid JWT: " + e.getMessage());
    }
  }

  private io.jsonwebtoken.Claims parseClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(signingKey)
        .setAllowedClockSkewSeconds(60)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
