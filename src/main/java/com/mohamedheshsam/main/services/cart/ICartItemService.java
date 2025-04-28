package com.mohamedheshsam.main.services.cart;

import com.mohamedheshsam.main.models.CartItem;
import com.mohamedheshsam.main.dtos.CartItemDto;

public interface ICartItemService {
  void addItemToCart(Long cartId, Long productId, int quantity);

  void removeItemFromCart(Long cartId, Long productId);

  void updateItemQuantity(Long cartId, Long productId, int quantity);

  CartItem getCartItem(Long cartId, Long productId);

  CartItemDto convertToDto(CartItem cartItem);
}
