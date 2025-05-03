package com.mohamedheshsam.main.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CartItemDto {
  private Long id;
  private int quantity;
  private BigDecimal unitPrice;
  private BigDecimal totalPrice;
  private ProductDto product;
  private List<ImageDto> imageUrls;

  // Getters and setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(BigDecimal totalPrice) {
    this.totalPrice = totalPrice;
  }

  public ProductDto getProduct() {
    return product;
  }

  public void setProduct(ProductDto product) {
    this.product = product;
  }

  public List<ImageDto> getImageUrls() {
    return imageUrls;
  }

  public void setImageUrls(List<ImageDto> imageUrls) {
    this.imageUrls = imageUrls;
  }
}
