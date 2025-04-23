package com.mohamedheshsam.main.services.cart;

import java.math.BigDecimal;

import com.mohamedheshsam.main.dtos.CartDto;
import com.mohamedheshsam.main.models.Cart;

public interface ICartService {
  CartDto getCart(Long id);

  Cart getCartEntity(Long id);

  void clearCart(Long id);

  BigDecimal getTotalPrice(Long id);

  Long initializeNewCart();

}
