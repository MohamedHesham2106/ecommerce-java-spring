package com.mohamedheshsam.main.dtos;

import java.util.List;

import com.mohamedheshsam.main.enums.Gender;
import com.mohamedheshsam.main.enums.RoleType;

import lombok.Data;

@Data
public class UserDto {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private List<OrderDto> orders;
  private CartDto cart;
  private RoleType role;
  private ImageDto image;
  private Gender gender;
}
