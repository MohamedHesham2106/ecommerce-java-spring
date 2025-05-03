package com.mohamedheshsam.main.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "auth.token")
public class JwtProperties {
  private Duration expiration;
  private String jwtSecret;

  public Duration getExpiration() {
    return expiration;
  }

  public void setExpiration(Duration expiration) {
    this.expiration = expiration;
  }

  public String getJwtSecret() {
    return jwtSecret;
  }

  public void setJwtSecret(String jwtSecret) {
    this.jwtSecret = jwtSecret;
  }
}
