package com.mohamedheshsam.main.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.mohamedheshsam.main.enums.RoleType;
import com.mohamedheshsam.main.security.user.ShopUserDetails;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

  @Value("${auth.token.jwtSecret}")
  private String jwtSecret;

  @Value("${auth.token.expirationInMils}")
  private int expirationTime;

  public String generateTokenForUser(Authentication authentication) {
    ShopUserDetails userPrincipal = (ShopUserDetails) authentication.getPrincipal();

    RoleType role = userPrincipal.getAuthorities().stream().findFirst()
        .map(authority -> RoleType.valueOf(authority.getAuthority())).orElse(null);

    return Jwts.builder()
        .setSubject(userPrincipal.getEmail())
        .claim("id", userPrincipal.getId())
        .claim("role", role)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + expirationTime))
        .signWith(key(), SignatureAlgorithm.HS256).compact();

  }

  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  public String getUsernameFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key())
        .build()
        .parseClaimsJws(token)
        .getBody().getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(key())
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException
        | IllegalArgumentException e) {
      throw new JwtException(e.getMessage());

    }
  }
}
