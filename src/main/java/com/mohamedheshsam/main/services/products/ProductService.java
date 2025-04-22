package com.mohamedheshsam.main.services.products;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mohamedheshsam.main.exceptions.ProductNotFoundException;
import com.mohamedheshsam.main.models.Category;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.requests.AddProductRequestDto;
import com.mohamedheshsam.main.requests.ProductUpdateRequest;
import com.mohamedheshsam.main.respository.CategoryRepository;
import com.mohamedheshsam.main.respository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public Product addProduct(AddProductRequestDto product) {
    // check if category exists
    Category category = categoryRepository.findByName(product.getCategory().getName())
        .orElseGet(() -> {
          Category newCategory = new Category();
          newCategory.setName(product.getCategory().getName());
          return categoryRepository.save(newCategory);
        });
    product.setCategory(category);
    return productRepository.save(createProduct(product, category));
  }

  @Override
  public Product updateProduct(ProductUpdateRequest product, Long id) {
    return productRepository.findById(id)
        .map(existingProduct -> updateExistingProduct(existingProduct, product))
        .map(productRepository::save)
        .orElseThrow(() -> new ProductNotFoundException("Product not found"));
  }

  @Override
  public Product getProductById(Long id) {
    return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found"));
  }

  @Override
  public void deleteProductById(Long id) {
    productRepository.findById(id).ifPresentOrElse(productRepository::delete,
        () -> {
          throw new ProductNotFoundException("Product not found");
        });

  }

  @Override
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @Override
  public List<Product> getProductsByCategory(String category) {
    return productRepository.findByCategoryName(category);
  }

  @Override
  public List<Product> getProductsByBrand(String brand) {
    return productRepository.findByBrandName(brand);
  }

  @Override
  public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
    return productRepository.findProductsByCategoryNameAndBrand(category, brand);
  }

  @Override
  public List<Product> getProductsByName(String name) {
    return productRepository.findByName(name);
  }

  @Override
  public List<Product> getProductsByBrandAndName(String brand, String name) {
    return productRepository.findByBrandAndName(brand, name);
  }

  @Override
  public Long countProductsByBrandAndName(String brand, String name) {
    return productRepository.countByBrandAndName(brand, name);
  }

  // Helpers

  private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest productUpdateRequest) {
    existingProduct.setName(productUpdateRequest.getName());
    existingProduct.setBrand(productUpdateRequest.getBrand());
    existingProduct.setPrice(productUpdateRequest.getPrice());
    existingProduct.setInventory(productUpdateRequest.getInventory());
    existingProduct.setDescription(productUpdateRequest.getDescription());
    Category category = categoryRepository.findByName(productUpdateRequest.getCategory().getName())
        .orElseThrow(() -> new ProductNotFoundException("Category not found"));
    existingProduct.setCategory(category);
    return existingProduct;
  }

  private Product createProduct(AddProductRequestDto product, Category category) {
    return new Product(product.getName(), product.getBrand(), product.getPrice(), product.getInventory(),
        product.getDescription(), category);
  }
}
