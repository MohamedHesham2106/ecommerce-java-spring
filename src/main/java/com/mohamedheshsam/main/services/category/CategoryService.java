package com.mohamedheshsam.main.services.category;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mohamedheshsam.main.exceptions.AlreadyExistException;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Category;
import com.mohamedheshsam.main.respository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
  private final CategoryRepository categoryRepository;

  @Override
  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
  }

  @Override
  public Category getCategoryByName(String name) {
    return categoryRepository.findByNameIgnoreCase(name);
  }

  @Override
  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }

  @Override
  public Category addCategory(Category category) {
    return Optional.of(category).filter(c -> !categoryRepository.existsByNameIgnoreCase(c.getName()))
        .map(categoryRepository::save)
        .orElseThrow(() -> new AlreadyExistException(category.getName() + " already exists"));
  }

  @Override
  public Category updateCategory(Category category, Long id) {
    return Optional.ofNullable(getCategoryById(id)).map(oldCategory -> {
      oldCategory.setName(category.getName());
      return categoryRepository.save(oldCategory);
    }).orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
  }

  @Override
  public void deleteCategoryById(Long id) {
    categoryRepository.findById(id)
        .ifPresentOrElse(categoryRepository::delete, () -> {
          throw new ResourceNotFoundException("Category not found!");
        });

  }
}
