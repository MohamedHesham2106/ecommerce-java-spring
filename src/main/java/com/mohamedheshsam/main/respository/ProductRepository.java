package com.mohamedheshsam.main.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mohamedheshsam.main.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

  // single-field, case-insensitive
  List<Product> findByCategoryNameIgnoreCase(String category);

  List<Product> findByBrandIgnoreCase(String brand);

  List<Product> findByNameIgnoreCase(String name);

  List<Product> findByCategoryNameIgnoreCaseAndBrandIgnoreCase(String category, String brand);

  List<Product> findByBrandIgnoreCaseAndNameIgnoreCase(String brand, String name);

  List<Product> findByCategoryNameIgnoreCaseAndBrandIgnoreCaseAndNameIgnoreCase(
      String category,
      String brand,
      String name);

  long countByBrandIgnoreCaseAndNameIgnoreCase(String brand, String name);
}
