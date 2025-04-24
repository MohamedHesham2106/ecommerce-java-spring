package com.mohamedheshsam.main.services.user;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.mohamedheshsam.main.dtos.UserDto;
import com.mohamedheshsam.main.exceptions.AlreadyExistException;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.requests.CreateUserRequest;
import com.mohamedheshsam.main.requests.UserUpdateRequest;
import com.mohamedheshsam.main.respository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

  @Override
  public User getUserById(Long userId) {
    return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  @Override
  public User createUser(CreateUserRequest request) {
    return Optional.of(request).filter(user -> !userRepository.existsByEmail(user.getEmail()))
        .map(req -> {
          User user = new User();
          user.setEmail(request.getEmail());
          user.setPassword(request.getPassword());
          user.setFirstName(request.getFirstName());
          user.setLastName(request.getLastName());
          return userRepository.save(user);
        }).orElseThrow(() -> new AlreadyExistException("Oops!" + request.getEmail() + " already exists!"));
  }

  @Override
  public User updateUser(UserUpdateRequest request, Long userId) {
    return userRepository.findById(userId).map(user -> {
      user.setFirstName(request.getFirstName());
      user.setLastName(request.getLastName());
      return userRepository.save(user);
    }).orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  @Override
  public void deleteUser(Long userId) {
    userRepository.findById(userId).map(user -> {
      userRepository.delete(user);
      return null;
    }).orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  @Override
  public UserDto convertUserToDto(User user) {
    return modelMapper.map(user, UserDto.class);
  }

}
