package com.mohamedheshsam.main.services.products;

import java.util.List;

import com.mohamedheshsam.main.dtos.ProductDto;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.requests.AddProductRequestDto;
import com.mohamedheshsam.main.requests.ProductUpdateRequest;

public interface IProductService {

  /**
   * Create a new product with the given details.
   */
  Product addProduct(AddProductRequestDto product);

  /**
   * Retrieve a single product by its ID.
   */
  Product getProductById(Long id);

  /**
   * Delete a product by its ID.
   */
  void deleteProductById(Long id);

  /**
   * Update an existing product by its ID with new details.
   */
  Product updateProduct(ProductUpdateRequest product, Long productId);

  /**
   * Retrieve all products (no filtering).
   */
  List<Product> getAllProducts();

  /**
   * Retrieve products filtered optionally by category, brand, and/or name.
   * Passing null for any parameter means "no filter" on that field.
   */
  List<Product> getFilteredProducts(String category, String brand, String name, Boolean isFeatured);

  /**
   * Convert a list of Product entities to their DTO representations.
   */
  List<ProductDto> getConvertedProducts(List<Product> products);

  /**
   * Convert a single Product entity to its DTO representation.
   */
  ProductDto convertToDto(Product product);

}
