package com.mohamedheshsam.main.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mohamedheshsam.main.dtos.ProductDto;
import com.mohamedheshsam.main.exceptions.ProductNotFoundException;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.requests.AddProductRequestDto;
import com.mohamedheshsam.main.requests.ProductUpdateRequest;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.services.products.IProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {
  private final IProductService productService;

  @GetMapping()
  public ResponseEntity<ApiResponse> getAllProducts() {
    List<Product> products = productService.getAllProducts();
    List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
    return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
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
  @PostMapping()
  public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequestDto product) {
    try {
      Product savedProduct = productService.addProduct(product);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse("Product added successfully", savedProduct));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to add product", null));
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id,
      @RequestBody ProductUpdateRequest product) {
    try {
      Product updatedProduct = productService.updateProduct(product, id);
      return ResponseEntity.ok(new ApiResponse("Product updated successfully", updatedProduct));
    } catch (ProductNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse(e.getMessage(), null));
    } catch (Exception e) {
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

  @GetMapping(params = { "brand", "name" })
  public ResponseEntity<ApiResponse> getProductByBrandAndName(
      @RequestParam String brand,
      @RequestParam String name) {
    List<Product> products = productService.getProductsByBrandAndName(brand, name);
    if (products.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse("No products found", null));
    }
    List<ProductDto> productDtos = productService.getConvertedProducts(products);
    return ResponseEntity.ok(new ApiResponse("Products retrieved successfully", productDtos));
  }

  @GetMapping(params = { "category", "brand" })
  public ResponseEntity<ApiResponse> getProductByCategoryAndBrand(
      @RequestParam String category,
      @RequestParam String brand) {
    try {
      List<Product> products = productService.getProductsByCategoryAndBrand(category, brand);
      if (products.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse("No products found", null));
      }
      List<ProductDto> productDtos = productService.getConvertedProducts(products);
      return ResponseEntity.ok(new ApiResponse("Products retrieved successfully", productDtos));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to retrieve products", null));
    }
  }

  @GetMapping(params = { "name" })
  public ResponseEntity<ApiResponse> getProductByName(@RequestParam String name) {
    try {
      List<Product> products = productService.getProductsByName(name);
      if (products.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse("No products found", null));
      }
      List<ProductDto> productDtos = productService.getConvertedProducts(products);
      return ResponseEntity.ok(new ApiResponse("Products retrieved successfully", productDtos));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to retrieve products", null));
    }
  }

  @GetMapping(params = { "brand" })
  public ResponseEntity<ApiResponse> getProductByBrand(@RequestParam String brand) {
    try {
      List<Product> products = productService.getProductsByBrand(brand);
      if (products.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse("No products found", null));
      }
      List<ProductDto> productDtos = productService.getConvertedProducts(products);
      return ResponseEntity.ok(new ApiResponse("Products retrieved successfully", productDtos));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to retrieve products", null));
    }
  }

  @GetMapping(params = { "category" })
  public ResponseEntity<ApiResponse> getProductByCategory(@RequestParam String category) {
    try {
      List<Product> products = productService.getProductsByCategory(category);
      if (products.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse("No products found", null));
      }
      List<ProductDto> productDtos = productService.getConvertedProducts(products);
      return ResponseEntity.ok(new ApiResponse("Products retrieved successfully", productDtos));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to retrieve products", null));
    }
  }

  @GetMapping(value = "/count", params = { "brand", "name" })
  public ResponseEntity<ApiResponse> countProductsByBrandAndName(@RequestParam String brand,
      @RequestParam String name) {
    try {
      long count = productService.countProductsByBrandAndName(brand, name);
      return ResponseEntity.ok(new ApiResponse("Product count retrieved successfully", count));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to count products", null));
    }
  }
}
