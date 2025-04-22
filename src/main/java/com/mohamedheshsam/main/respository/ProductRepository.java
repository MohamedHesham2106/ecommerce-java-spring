package com.mohamedheshsam.main.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mohamedheshsam.main.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findByCategoryName(String category);

  List<Product> findByBrandName(String brand);

  List<Product> findProductsByCategoryNameAndBrand(String category, String brand);

  List<Product> findByName(String name);

  List<Product> findByBrandAndName(String brand, String name);

  Long countByBrandAndName(String brand, String name);
}
