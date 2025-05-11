package com.mohamedheshsam.main.dtos;

public class BrandCountDto {
  private String brand;
  private Long count;

  public BrandCountDto(String brand, Long count) {
    this.brand = brand;
    this.count = count;
  }

  public String getBrand() {
    return brand;
  }

  public Long getCount() {
    return count;
  }
}
