package com.mohamedheshsam.main.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CartDto {
  private Long id;
  private List<CartItemDto> items;
  private BigDecimal totalAmount;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<CartItemDto> getItems() {
    return items;
  }

  public void setItems(List<CartItemDto> items) {
    this.items = items;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }
}
