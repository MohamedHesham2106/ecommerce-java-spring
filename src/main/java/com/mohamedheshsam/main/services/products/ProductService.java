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
  public List<Product> getFilteredProducts(String category, String brand, String name) {
    if (category != null && brand != null && name != null) {
      return productRepository.findByCategoryNameIgnoreCaseAndBrandIgnoreCaseAndNameIgnoreCase(category, brand, name);
    } else if (category != null && brand != null) {
      return productRepository.findByCategoryNameIgnoreCaseAndBrandIgnoreCase(category, brand);
    } else if (brand != null && name != null) {
      return productRepository.findByBrandIgnoreCaseAndNameIgnoreCase(brand, name);
    } else if (category != null) {
      return productRepository.findByCategoryNameIgnoreCase(category);
    } else if (brand != null) {
      return productRepository.findByBrandIgnoreCase(brand);
    } else if (name != null) {
      return productRepository.findByNameIgnoreCase(name);
    } else {
      return productRepository.findAll();
    }
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

  private Product createProduct(AddProductRequestDto req, Category category) {
    return new Product(
        req.getName(),
        req.getBrand(),
        req.getPrice(),
        req.getInventory(),
        req.getDescription(),
        category);
  }

  private Product updateExistingProduct(Product existing, ProductUpdateRequest req) {
    existing.setName(req.getName());
    existing.setBrand(req.getBrand());
    existing.setPrice(req.getPrice());
    existing.setInventory(req.getInventory());
    existing.setDescription(req.getDescription());
    Category cat = Optional.ofNullable(
        categoryRepository.findByNameIgnoreCase(req.getCategory().getName()))
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    existing.setCategory(cat);
    return existing;
  }
}
