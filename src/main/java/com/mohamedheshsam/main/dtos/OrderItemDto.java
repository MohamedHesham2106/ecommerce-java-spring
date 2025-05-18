package com.mohamedheshsam.main.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class OrderItemDto {
  private Long id;
  private String name;
  private int quantity;
  private BigDecimal price;
  private List<ImageDto> images;
}
