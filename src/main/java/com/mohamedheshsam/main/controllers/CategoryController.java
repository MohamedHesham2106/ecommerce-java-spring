package com.mohamedheshsam.main.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mohamedheshsam.main.exceptions.AlreadyExistException;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Category;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.services.category.ICategoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
  private final ICategoryService categoryService;

  @GetMapping()
  public ResponseEntity<ApiResponse> getAllCategories() {
    try {
      return ResponseEntity
          .ok(new ApiResponse("Retrieved Categories successfully", categoryService.getAllCategories()));
    } catch (Exception e) {

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to retrieve categories", null));
    }
  }

  @PostMapping()
  public ResponseEntity<ApiResponse> addCategory(@RequestBody Category category) {
    try {
      Category savedCategory = categoryService.addCategory(category);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse("Category added successfully", savedCategory));
    } catch (AlreadyExistException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(new ApiResponse("Category already exists", null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to add category", null));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
    try {
      Category category = categoryService.getCategoryById(id);
      return ResponseEntity.ok(new ApiResponse("Category retrieved successfully", category));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse("Category not found", null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to retrieve category", null));
    }
  }

  @GetMapping(params = "name")
  public ResponseEntity<ApiResponse> getCategoryByName(@RequestParam String name) {
    try {
      Category category = categoryService.getCategoryByName(name);
      return ResponseEntity.ok(new ApiResponse("Category retrieved successfully", category));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse("Category not found", null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to retrieve category", null));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
    try {
      categoryService.deleteCategory(id);
      return ResponseEntity.ok(new ApiResponse("Category deleted successfully", null));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse("Category not found", null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to delete category", null));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse> updateCategory(@PathVariable Long id, @RequestBody Category category) {
    try {
      Category updatedCategory = categoryService.updateCategory(id, category);
      return ResponseEntity.ok(new ApiResponse("Category updated successfully", updatedCategory));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse("Category not found", null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to update category", null));
    }
  }
}
