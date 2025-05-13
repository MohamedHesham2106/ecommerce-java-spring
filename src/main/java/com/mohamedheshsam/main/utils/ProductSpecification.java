package com.mohamedheshsam.main.utils;

import com.mohamedheshsam.main.models.Product;

import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
  private ProductSpecification() {
  }

  public static Specification<Product> hasCategoryLike(String category) {
    return (root, query, cb) -> category == null ? null
        : cb.like(cb.lower(root.get("category").get("name")), "%" + category.toLowerCase() + "%");
  }

  public static Specification<Product> hasBrandLike(String brand) {
    return (root, query, cb) -> brand == null ? null
        : cb.like(cb.lower(root.get("brand")), "%" + brand.toLowerCase() + "%");
  }

  public static Specification<Product> hasNameLike(String name) {
    return (root, query, cb) -> name == null ? null
        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
  }

  public static Specification<Product> isFeatured(Boolean isFeatured) {
    return (root, query, cb) -> isFeatured == null ? null : cb.equal(root.get("isFeatured"), isFeatured);
  }
}
