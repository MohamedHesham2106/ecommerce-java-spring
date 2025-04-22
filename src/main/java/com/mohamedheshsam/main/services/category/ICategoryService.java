package com.mohamedheshsam.main.services.category;

import java.util.List;

import com.mohamedheshsam.main.models.Category;

public interface ICategoryService {
  Category getCategoryById(Long id);

  Category getCategoryByName(String name);

  List<Category> getAllCategories();

  Category addCategory(Category category);

  Category updateCategory(Long id, Category category);

  void deleteCategory(Long id);

}
