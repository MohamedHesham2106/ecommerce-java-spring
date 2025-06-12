package com.mohamedheshsam.main.security.config;

import java.util.List;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.mohamedheshsam.main.security.jwt.AuthTokenFilter;
import com.mohamedheshsam.main.security.jwt.JwtAuthEntryPoint;
import com.mohamedheshsam.main.security.user.ShopUserDetailsService;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class ShopConfig {
  private final ShopUserDetailsService userDetailsService;
  private final JwtAuthEntryPoint authEntryPoint;

  private static final List<String> SECURED_URLS = List.of("/api/v1/carts/**", "/api/v1/cartItems/**");

  @Value("${cors.allowed-origins:http://localhost:4200}")
  private String allowedOrigins;

  @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
  private String allowedMethods;

  @Value("${cors.allowed-headers:Origin,Content-Type,Accept,Authorization,X-Requested-With}")
  private String allowedHeaders;

  @Value("${cors.allow-credentials:true}")
  private boolean allowCredentials;

  @Value("${cors.max-age:3600}")
  private long maxAge;

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthTokenFilter authTokenFilter() {
    return new AuthTokenFilter();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    var authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(request -> {
          var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
          corsConfiguration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
          corsConfiguration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
          corsConfiguration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
          corsConfiguration.setExposedHeaders(Arrays.asList("Authorization"));
          corsConfiguration.setAllowCredentials(allowCredentials);
          corsConfiguration.setMaxAge(maxAge);
          return corsConfiguration;
        }))
        .csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth.requestMatchers(SECURED_URLS.toArray(String[]::new)).authenticated()
            .anyRequest().permitAll());
    http.authenticationProvider(daoAuthenticationProvider());
    http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
