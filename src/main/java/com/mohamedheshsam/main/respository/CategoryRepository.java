package com.mohamedheshsam.main.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mohamedheshsam.main.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  Optional<Category> findByName(String name);

  boolean existsByName(String name);

}
