package com.mohamedheshsam.main.services.user;

import java.util.Optional;
import java.util.Collections;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.mohamedheshsam.main.dtos.UserDto;
import com.mohamedheshsam.main.enums.RoleType;
import com.mohamedheshsam.main.exceptions.AlreadyExistException;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.models.Role;
import com.mohamedheshsam.main.requests.CreateUserRequest;
import com.mohamedheshsam.main.requests.UserUpdateRequest;
import com.mohamedheshsam.main.respository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import com.mohamedheshsam.main.dtos.OrderDto;
import com.mohamedheshsam.main.dtos.CartDto;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public User getUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
  }

  @Override
  public User createUser(CreateUserRequest request) {
    return Optional.of(request)
      .filter(user -> !userRepository.existsByEmail(request.getEmail()))
      .map(req -> {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        Role defaultRole = new Role(RoleType.USER);
        user.setRoles(Collections.singleton(defaultRole));
        return userRepository.save(user);
      }).orElseThrow(() -> new AlreadyExistException("The email " + request.getEmail() + " is already registered!"));
  }

  @Override
  public User updateUser(UserUpdateRequest request, Long userId) {
    return userRepository.findById(userId).map(existingUser -> {
      existingUser.setFirstName(request.getFirstName());
      existingUser.setLastName(request.getLastName());
      return userRepository.save(existingUser);
    }).orElseThrow(() -> new ResourceNotFoundException("User not found!"));

  }

  @Override
  public void deleteUser(Long userId) {
    userRepository.findById(userId).ifPresentOrElse(userRepository::delete, () -> {
      throw new ResourceNotFoundException("User not found!");
    });
  }

  @Override
  public UserDto convertUserToDto(User user) {
    UserDto userDto = modelMapper.map(user, UserDto.class);

    // Check if orders are not null before mapping
    if (user.getOrders() != null) {
      userDto.setOrders(user.getOrders().stream()
          .map(order -> modelMapper.map(order, OrderDto.class))
          .toList());
    } else {
      userDto.setOrders(Collections.emptyList());
    }

    // Check if cart is not null before mapping
    if (user.getCart() != null) {
      userDto.setCart(modelMapper.map(user.getCart(), CartDto.class));
    } else {
      userDto.setCart(null);
    }

    // Map roles to RoleType and set them in UserDto
    if (user.getRoles() != null) {
      userDto.setRoles(user.getRoles().stream()
          .map(role -> role.getName().name())
          .toList());
    } else {
      userDto.setRoles(Collections.emptyList());
    }

    return userDto;
  }

  @Override
  public User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    return userRepository.findByEmail(email);
  }

}
