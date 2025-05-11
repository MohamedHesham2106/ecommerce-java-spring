package com.mohamedheshsam.main.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.mohamedheshsam.main.dtos.BrandCountDto;
import com.mohamedheshsam.main.dtos.ProductDto;
import com.mohamedheshsam.main.exceptions.ProductNotFoundException;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.requests.AddProductRequestDto;
import com.mohamedheshsam.main.requests.ProductUpdateRequest;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.services.image.IImageService;
import com.mohamedheshsam.main.services.products.IProductService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {
  private final IProductService productService;
  private final IImageService imageService;

  @GetMapping
  public ResponseEntity<ApiResponse> getAllProducts(
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Boolean isFeatured,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit) {

    try {
      List<Product> filteredProducts = productService.getFilteredProducts(category, brand, name, isFeatured);

      int total = filteredProducts.size();
      int pages = (int) Math.ceil((double) total / limit);
      int offset = (page - 1) * limit;

      if (offset >= total) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse("No products found for this page", null));
      }

      List<Product> pagedProducts = filteredProducts.stream()
          .skip(offset)
          .limit(limit)
          .toList();

      List<ProductDto> productDtos = productService.getConvertedProducts(pagedProducts);

      var response = new java.util.HashMap<String, Object>();
      response.put("products", productDtos);
      response.put("meta", java.util.Map.of(
          "page", page,
          "pages", pages,
          "limit", limit,
          "offset", offset,
          "total", total));

      return ResponseEntity.ok(new ApiResponse("success", response));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to retrieve products", null));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
    try {
      Product product = productService.getProductById(id);
      ProductDto productDto = productService.convertToDto(product);
      return ResponseEntity.ok(new ApiResponse("success", productDto));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse> addProduct(
      @RequestPart("product") AddProductRequestDto product,
      @RequestPart(value = "images", required = false) List<MultipartFile> images) {
    try {
      Product savedProduct = productService.addProduct(product);

      if (images != null && !images.isEmpty()) {
        imageService.saveImages(images, savedProduct.getId());
      }

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse("Product and images added successfully", savedProduct));
    } catch (Exception e) {
      // Log the exception for debugging
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to add product", null));
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse> updateProduct(
      @PathVariable Long id,
      @RequestPart("product") ProductUpdateRequest updateRequest,
      @RequestPart(value = "images", required = false) List<MultipartFile> images) {
    try {
      Product updatedProduct = productService.updateProduct(updateRequest, id);

      if (images != null && !images.isEmpty()) {
        imageService.updateImages(images, id);
      }

      ProductDto updatedDto = productService.convertToDto(updatedProduct);
      return ResponseEntity.ok(new ApiResponse("Product and images updated successfully", updatedDto));
    } catch (ProductNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse(e.getMessage(), null));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to update product", null));
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
    try {
      productService.deleteProductById(id);
      return ResponseEntity.ok(new ApiResponse("Product deleted successfully", null));
    } catch (ProductNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse(e.getMessage(), null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to delete product", null));
    }
  }

  @GetMapping("/brands")
  public List<BrandCountDto> getBrandCounts() {
    return productService.getAllBrands();
  }

  @GetMapping("/count")
  public ResponseEntity<ApiResponse> getProductsCount() {
    HashMap<String, Long> response = new HashMap<>();
    long count = productService.countAllProducts();
    response.put("count", count);

    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse("success", response));
  }
}
