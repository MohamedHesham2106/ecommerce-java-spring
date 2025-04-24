package com.mohamedheshsam.main.services.user;

import com.mohamedheshsam.main.dtos.UserDto;
import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.requests.CreateUserRequest;
import com.mohamedheshsam.main.requests.UserUpdateRequest;

public interface IUserService {
  User getUserById(Long userId);

  User createUser(CreateUserRequest request);

  User updateUser(UserUpdateRequest request, Long userId);

  void deleteUser(Long userId);

  UserDto convertUserToDto(User user);
}
