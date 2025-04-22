package com.mohamedheshsam.main.services.category;

import java.util.List;

import com.mohamedheshsam.main.models.Category;

public interface ICategoryService {
  Category getCategoryById(Long id);

  Category getCategoryByName(String name);

  List<Category> getAllCategories();

  Category addCategory(Category category);

  Category updateCategory(Category category, Long id);

  void deleteCategoryById(Long id);

}
