package com.mohamedheshsam.main.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mohamedheshsam.main.dtos.UserDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.requests.UserUpdateRequest;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.services.user.IUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
  private final IUserService userService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<ApiResponse> getAllUsers() {
    try {
      Map<String, List<UserDto>> responseData = new HashMap<>();
      responseData.put("users", userService.getAllUsers());
      return ResponseEntity.ok(new ApiResponse("Success", responseData));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Error fetching users", null));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
    try {
      User user = userService.getUserById(id);
      UserDto userDto = userService.convertUserToDto(user);
      return ResponseEntity.ok(new ApiResponse("Success", userDto));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse> updateUser(@RequestBody UserUpdateRequest request, @PathVariable Long id) {
    try {
      User user = userService.updateUser(request, id);
      UserDto userDto = userService.convertUserToDto(user);
      return ResponseEntity.ok(new ApiResponse("Update User Success!", userDto));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
    try {
      userService.deleteUser(id);
      return ResponseEntity.ok(new ApiResponse("Delete User Success!", null));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    }
  }
}
