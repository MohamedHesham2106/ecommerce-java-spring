package com.mohamedheshsam.main.services.products;

import java.util.List;

import com.mohamedheshsam.main.dtos.ProductDto;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.requests.AddProductRequestDto;
import com.mohamedheshsam.main.requests.ProductUpdateRequest;

public interface IProductService {
  Product addProduct(AddProductRequestDto product);

  Product getProductById(Long id);

  void deleteProductById(Long id);

  Product updateProduct(ProductUpdateRequest product, Long productId);

  List<Product> getAllProducts();

  List<Product> getProductsByCategory(String category);

  List<Product> getProductsByBrand(String brand);

  List<Product> getProductsByCategoryAndBrand(String category, String brand);

  List<Product> getProductsByName(String name);

  List<Product> getProductsByBrandAndName(String brand, String name);

  Long countProductsByBrandAndName(String brand, String name);

  List<ProductDto> getConvertedProducts(List<Product> products);

  ProductDto convertToDto(Product product);

}
