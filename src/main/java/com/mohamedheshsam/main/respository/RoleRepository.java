package com.mohamedheshsam.main.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mohamedheshsam.main.models.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String role);
}
