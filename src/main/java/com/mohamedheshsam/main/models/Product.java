package com.mohamedheshsam.main.models;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String brand;
  private BigDecimal price;
  private int inventory;
  private String description;

  // category doesn't depend on product nor vice versa
  @ManyToOne(cascade = CascadeType.ALL)
  // foreign key in product table
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  // remove images if orphaned
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Image> images;

  public Product(String name, String brand, BigDecimal price, int inventory, String description, Category category) {
    this.name = name;
    this.brand = brand;
    this.price = price;
    this.inventory = inventory;
    this.description = description;
    this.category = category;
  }
}
