package com.mohamedheshsam.main.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import com.mohamedheshsam.main.models.Category;

@Data
public class ProductDto {
  private Long id;
  private String name;
  private String brand;
  private BigDecimal price;
  private int inventory;
  private String description;
  private Category category;
  private List<ImageDto> images;
}
