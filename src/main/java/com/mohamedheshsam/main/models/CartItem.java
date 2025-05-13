package com.mohamedheshsam.main.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mohamedheshsam.main.dtos.ProductDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CartItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private int quantity;
  private BigDecimal unitPrice;
  private BigDecimal totalPrice;

  @Transient
  private ProductDto productDto;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne(cascade = CascadeType.ALL)
  @JsonIgnore
  private Cart cart;

  public CartItem(Cart cart, Product product, int quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
    this.cart = cart;
    this.product = product;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.totalPrice = totalPrice;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public ProductDto getProductDto() {
    return productDto;
  }

  public void setProductDto(ProductDto productDto) {
    this.productDto = productDto;
  }

  public void setTotalPrice() {
    this.totalPrice = this.unitPrice.multiply(new BigDecimal(quantity));
  }
}
