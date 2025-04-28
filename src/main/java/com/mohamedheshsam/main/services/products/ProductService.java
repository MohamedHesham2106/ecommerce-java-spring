package com.mohamedheshsam.main.services.products;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import com.mohamedheshsam.main.exceptions.ImageConversionException;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ModelMapper modelMapper;
  private final ImageRepository imageRepository;

  @Override
  public Product addProduct(AddProductRequestDto request) {
    // check if the category is found in the DB
    // If Yes, set it as the new product category
    // If No, the save it as a new category
    // The set as the new product category.

    Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
        .orElseGet(() -> {
          Category newCategory = new Category(request.getCategory().getName());
          return categoryRepository.save(newCategory);
        });
    request.setCategory(category);
    return productRepository.save(createProduct(request, category));
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
    return productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));
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
    return productRepository.findByBrand(brand);
  }

  @Override
  public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
    return productRepository.findByCategoryNameAndBrand(category, brand);
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

  private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request) {
    existingProduct.setName(request.getName());
    existingProduct.setBrand(request.getBrand());
    existingProduct.setPrice(request.getPrice());
    existingProduct.setInventory(request.getInventory());
    existingProduct.setDescription(request.getDescription());

    Category category = categoryRepository.findByName(request.getCategory().getName());
    existingProduct.setCategory(category);
    return existingProduct;

  }

  private Product createProduct(AddProductRequestDto request, Category category) {
    return new Product(
        request.getName(),
        request.getBrand(),
        request.getPrice(),
        request.getInventory(),
        request.getDescription(),
        category);
  }

  @Override
  public List<ProductDto> getConvertedProducts(List<Product> products) {
    return products.stream().map(this::convertToDto).toList();
  }

  @Override
  public ProductDto convertToDto(Product product) {
    ProductDto productDto = modelMapper.map(product, ProductDto.class);
    List<Image> images = imageRepository.findByProductId(product.getId());
    List<String> base64Images = images.stream()
        .map(image -> {
          try {
            byte[] imageBytes = image.getImage().getBytes(1, (int) image.getImage().length());
            return java.util.Base64.getEncoder().encodeToString(imageBytes);
          } catch (Exception e) {
            throw new ImageConversionException("Failed to convert image to Base64", e);
          }
        })
        .toList();
    productDto.setBase64Images(base64Images);
    return productDto;
  }

  @Override
  public void saveProduct(Product product) {
    productRepository.save(product);
  }
}
