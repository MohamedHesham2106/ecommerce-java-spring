package com.mohamedheshsam.main.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDto {
  private Long id;
  private Long userId;
  private BigDecimal totalAmount;
  private String status;
  private List<OrderItemDto> items;
}
