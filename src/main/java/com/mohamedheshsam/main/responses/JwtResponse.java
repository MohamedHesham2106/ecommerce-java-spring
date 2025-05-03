package com.mohamedheshsam.main.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.mohamedheshsam.main.enums.RoleType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
  private String token;
}
