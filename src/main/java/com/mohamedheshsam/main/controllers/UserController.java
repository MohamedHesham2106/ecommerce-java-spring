package com.mohamedheshsam.main.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mohamedheshsam.main.dtos.UserDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Image;
import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.requests.UserUpdateRequest;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.services.user.IUserService;
import com.mohamedheshsam.main.services.image.IImageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
  private final IUserService userService;
  private final IImageService imageService;

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

  // Profile update without ID in path - authenticated user's profile
  @Transactional
  @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse> updateProfile(
      @RequestPart(value = "user", required = false) UserUpdateRequest request,
      @RequestPart(value = "image", required = false) MultipartFile imageFile) {
    try {
      Long userId = userService.getAuthenticatedUser().getId();
      User user;

      // Handle the case where only an image is provided
      if (request == null && imageFile != null && !imageFile.isEmpty()) {
        user = userService.getUserById(userId);
      } else if (request != null) {
        user = userService.updateUser(request, userId);
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse("No update data provided", null));
      }

      // Handle image upload if provided - ImageService now handles the existing image
      // correctly
      if (imageFile != null && !imageFile.isEmpty()) {
        Image newImage = imageService.saveUserImage(imageFile, userId);
        user.setImage(newImage);
        user = userService.saveUser(user);
      }

      UserDto userDto = userService.convertUserToDto(user);
      return ResponseEntity.ok(new ApiResponse("Update User Success!", userDto));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Error updating user profile: " + e.getMessage(), null));
    }
  }

  // Admin endpoint for updating any user by ID
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse> updateUser(
      @RequestPart("user") UserUpdateRequest request,
      @RequestPart(value = "image", required = false) MultipartFile imageFile,
      @PathVariable Long id) {
    try {
      User user = userService.updateUser(request, id);

      // Handle image upload if provided - ImageService now handles the existing image
      // correctly
      if (imageFile != null && !imageFile.isEmpty()) {
        Image newImage = imageService.saveUserImage(imageFile, id);
        user.setImage(newImage);
        user = userService.saveUser(user);
      }

      UserDto userDto = userService.convertUserToDto(user);
      return ResponseEntity.ok(new ApiResponse("Update User Success!", userDto));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Error updating user profile: " + e.getMessage(), null));
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
