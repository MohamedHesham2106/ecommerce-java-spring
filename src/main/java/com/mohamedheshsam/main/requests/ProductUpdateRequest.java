package com.mohamedheshsam.main.requests;

import java.math.BigDecimal;

import com.mohamedheshsam.main.models.Category;

import lombok.Data;

@Data
public class ProductUpdateRequest {
  private Long id;
  private String name;
  private String brand;
  private BigDecimal price;
  private int inventory;
  private String description;
  private Category category;
}
