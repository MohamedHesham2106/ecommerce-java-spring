package com.mohamedheshsam.main.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mohamedheshsam.main.dtos.UserDto;
import com.mohamedheshsam.main.exceptions.AlreadyExistException;
import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.requests.CreateUserRequest;
import com.mohamedheshsam.main.requests.LoginRequest;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.responses.JwtResponse;
import com.mohamedheshsam.main.security.jwt.JwtUtils;
import com.mohamedheshsam.main.security.user.ShopUserDetails;
import com.mohamedheshsam.main.services.user.IUserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final IUserService userService;
  private final JwtUtils jwtUtils;

  @PostMapping("signup")
  public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request) {
    try {
      User user = userService.createUser(request);
      UserDto userDto = userService.convertUserToDto(user);
      return ResponseEntity.ok(new ApiResponse("Create User Success!", userDto));
    } catch (AlreadyExistException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
    }
  }

  @PostMapping("login")
  public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
    try {
      Authentication authentication = authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(
              request.getEmail(), request.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = jwtUtils.generateTokenForUser(authentication);
      ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
      JwtResponse jwtResponse = new JwtResponse(userDetails.getId(), jwt, userDetails.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority).toList());
      return ResponseEntity.ok(new ApiResponse("Login Success!", jwtResponse));
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
    }

  }

}
