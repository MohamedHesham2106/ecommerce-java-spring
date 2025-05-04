package com.mohamedheshsam.main.services.products;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mohamedheshsam.main.dtos.ImageDto;
import com.mohamedheshsam.main.dtos.ProductDto;
import com.mohamedheshsam.main.exceptions.ProductNotFoundException;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Category;
import com.mohamedheshsam.main.models.Image;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.requests.AddProductRequestDto;
import com.mohamedheshsam.main.requests.ProductUpdateRequest;
import com.mohamedheshsam.main.respository.CategoryRepository;
import com.mohamedheshsam.main.respository.ImageRepository;
import com.mohamedheshsam.main.respository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ImageRepository imageRepository;

  @Override
  public Product addProduct(AddProductRequestDto request) {
    if (request.getCategory() == null || request.getCategory().getName() == null) {
      throw new IllegalArgumentException("Category name cannot be null");
    }

    Category category = Optional.ofNullable(
        categoryRepository.findByNameIgnoreCase(request.getCategory().getName()))
        .orElseGet(() -> {
          Category newCat = new Category(request.getCategory().getName());
          return categoryRepository.save(newCat);
        });
    request.getCategory().setName(category.getName());
    return productRepository.save(createProduct(request, category));
  }

  @Override
  public Product updateProduct(ProductUpdateRequest request, Long id) {
    return productRepository.findById(id)
        .map(existing -> updateExistingProduct(existing, request))
        .map(productRepository::save)
        .orElseThrow(() -> new ProductNotFoundException("Product not found"));
  }

  @Override
  public Product getProductById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));
  }

  @Override
  public void deleteProductById(Long id) {
    productRepository.findById(id)
        .ifPresentOrElse(
            productRepository::delete,
            () -> {
              throw new ProductNotFoundException("Product not found");
            });
  }

  @Override
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @Override
  public List<Product> getFilteredProducts(String category, String brand, String name, Boolean isFeatured) {
    List<Product> filtered = internalGetFilteredProducts(category, brand, name);

    if (Boolean.TRUE.equals(isFeatured)) {
      filtered = filtered.stream()
          .filter(p -> Boolean.TRUE.equals(p.getIsFeatured()))
          .toList();
    }
    return filtered;
  }

  @Override
  public List<ProductDto> getConvertedProducts(List<Product> products) {
    return products.stream().map(this::convertToDto).toList();
  }

  @Override
  public ProductDto convertToDto(Product product) {
    ProductDto dto = new ProductDto();
    dto.setId(product.getId());
    dto.setName(product.getName());
    dto.setBrand(product.getBrand());
    dto.setPrice(product.getPrice());
    dto.setInventory(product.getInventory());
    dto.setDescription(product.getDescription());
    dto.setCategory(product.getCategory());
    dto.setIsFeatured(product.getIsFeatured() != null && product.getIsFeatured());

    List<Image> images = imageRepository.findByProductId(product.getId());
    List<ImageDto> imageDtos = images.stream().map(img -> {
      ImageDto idto = new ImageDto();
      idto.setId(img.getId());
      idto.setFileName(img.getFileName());
      idto.setImageUrl(img.getImageUrl());
      return idto;
    }).toList();
    dto.setImages(imageDtos);

    return dto;
  }

  // --- Helpers ---
  private List<Product> internalGetFilteredProducts(String category, String brand, String name) {
    if (category == null && brand == null && name == null) {
      return productRepository.findAll();
    }
    return productRepository.findAll().stream()
        .filter(product -> category == null || product.getCategory().getName().equalsIgnoreCase(category))
        .filter(product -> brand == null || product.getBrand().equalsIgnoreCase(brand))
        .filter(product -> name == null || product.getName().equalsIgnoreCase(name))
        .toList();
  }

  private Product createProduct(AddProductRequestDto req, Category category) {
    return new Product(
        req.getName(),
        req.getBrand(),
        req.getPrice(),
        req.getInventory(),
        req.getDescription(),
        category, req.getIsFeatured() != null && req.getIsFeatured());
  }

  private Product updateExistingProduct(Product existing, ProductUpdateRequest request) {
    Optional.ofNullable(request.getName()).ifPresent(existing::setName);
    Optional.ofNullable(request.getDescription()).ifPresent(existing::setDescription);
    Optional.ofNullable(request.getPrice()).ifPresent(existing::setPrice);
    Optional.ofNullable(request.getIsFeatured()).ifPresent(existing::setIsFeatured);

    if (request.getCategory() != null) {
      Category category = categoryRepository.findById(request.getCategory().getId())
          .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
      existing.setCategory(category);
    }

    return existing;
  }
}
