package com.mohamedheshsam.main.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.mohamedheshsam.main.dtos.UserDto;
import com.mohamedheshsam.main.exceptions.AlreadyExistException;
import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.requests.CreateUserRequest;
import com.mohamedheshsam.main.requests.LoginRequest;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.responses.JwtResponse;
import com.mohamedheshsam.main.security.jwt.JwtUtils;
import com.mohamedheshsam.main.services.user.IUserService;
import com.mohamedheshsam.main.services.image.IImageService;
import com.mohamedheshsam.main.respository.UserRepository;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final IUserService userService;
  private final JwtUtils jwtUtils;
  private final IImageService imageService;
  private final UserRepository userRepository;

  @PostMapping(value = "register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse> createUser(
      @RequestPart("user") CreateUserRequest request,
      @RequestPart(value = "image", required = false) MultipartFile imageFile) {
    try {
      User user = userService.createUser(request);
      if (imageFile != null && !imageFile.isEmpty()) {
        var image = imageService.saveUserImage(imageFile, user.getId());
        user.setImage(image);
        userRepository.save(user);
      }
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
      return ResponseEntity.ok(new ApiResponse("Login Success!",
          new JwtResponse(jwt)));
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
    }
  }

}
