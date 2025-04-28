package com.mohamedheshsam.main.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mohamedheshsam.main.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);

  User findByEmail(String email);
}
