package com.mohamedheshsam.main.services.cart;

import com.mohamedheshsam.main.dtos.CartDto;
import com.mohamedheshsam.main.models.CartItem;

public interface ICartItemService {
  void addItemToCart(Long cartId, Long productId, int quantity);

  CartDto removeItemFromCart(Long cartId, Long productId, Integer quantity);

  void updateItemQuantity(Long cartId, Long productId, int quantity);

  CartItem getCartItem(Long cartId, Long productId);
}
