package com.mohamedheshsam.main.requests;

import com.mohamedheshsam.main.enums.Gender;

import lombok.Data;

@Data
public class CreateUserRequest {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private Gender gender;
}
