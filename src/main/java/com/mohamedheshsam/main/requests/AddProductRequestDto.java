package com.mohamedheshsam.main.requests;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mohamedheshsam.main.dtos.CategoryDto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class AddProductRequestDto {
  private Long id;
  private String name;
  private String brand;
  private BigDecimal price;
  private int inventory;
  private String description;

  @NotNull(message = "Category cannot be null")
  private CategoryDto category;

  private List<MultipartFile> images;
}
