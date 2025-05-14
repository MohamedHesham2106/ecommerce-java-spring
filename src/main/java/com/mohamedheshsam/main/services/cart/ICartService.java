package com.mohamedheshsam.main.services.cart;

import java.math.BigDecimal;

import com.mohamedheshsam.main.dtos.CartDto;
import com.mohamedheshsam.main.models.Cart;
import com.mohamedheshsam.main.models.User;

public interface ICartService {
  Cart getCart(Long id);

  void clearCart(Long id);

  BigDecimal getItemsCount(Long id);

  Cart initializeNewCart(User user);

  Cart getCartByUserId(Long userId);

  CartDto convertToDto(Cart cart);
}
