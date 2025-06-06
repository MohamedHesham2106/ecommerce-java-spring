package com.mohamedheshsam.main.services.user;

import java.util.List;

import com.mohamedheshsam.main.dtos.UserDto;
import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.requests.CreateUserRequest;
import com.mohamedheshsam.main.requests.UserUpdateRequest;

public interface IUserService {
  List<UserDto> getAllUsers();

  User getUserById(Long userId);

  User createUser(CreateUserRequest request);

  User updateUser(UserUpdateRequest request, Long userId);

  User saveUser(User user);

  void deleteUser(Long userId);

  UserDto convertUserToDto(User user);

  User getAuthenticatedUser();
}
