package com.mohamedheshsam.main.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mohamedheshsam.main.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  Category findByName(String name);

  boolean existsByName(String name);

}
