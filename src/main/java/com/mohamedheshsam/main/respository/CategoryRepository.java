package com.mohamedheshsam.main.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mohamedheshsam.main.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  /**
   * Find a category by name, case-insensitive.
   */
  Category findByNameIgnoreCase(String name);

  /**
   * Check if a category exists by name, case-insensitive.
   */
  boolean existsByNameIgnoreCase(String name);
}
