package com.mohamedheshsam.main.services.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.mohamedheshsam.main.dtos.CartDto;
import com.mohamedheshsam.main.dtos.CartItemDto;
import com.mohamedheshsam.main.dtos.ProductDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Cart;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.respository.CartItemRepository;
import com.mohamedheshsam.main.respository.CartRepository;
import com.mohamedheshsam.main.services.products.IProductService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final IProductService productService;

  @Override
  public CartDto getCart(Long id) {
    Cart cart = cartRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

    CartDto cartDto = new CartDto();
    cartDto.setId(cart.getId());
    cartDto.setTotalAmount(cart.getTotalAmount());

    List<CartItemDto> itemDtos = cart.getItems().stream().map(item -> {
      CartItemDto itemDto = new CartItemDto();
      itemDto.setId(item.getId());
      itemDto.setQuantity(item.getQuantity());
      itemDto.setUnitPrice(item.getUnitPrice());
      itemDto.setTotalPrice(item.getTotalPrice());
      itemDto.setProduct(productService.convertToDto(item.getProduct()));
      return itemDto;
    }).toList();

    cartDto.setItems(itemDtos);
    return cartDto;
  }

  @Transactional
  @Override
  public void clearCart(Long id) {
    Cart cart = cartRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

    // Delete all cart items
    cartItemRepository.deleteAllByCartId(id);

    // Clear the cart's items list
    cart.getItems().clear();

    // Delete the cart itself
    cartRepository.deleteById(id);
  }

  @Override
  public BigDecimal getTotalPrice(Long id) {
    Cart cart = cartRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    return cart.getTotalAmount();
  }

  @Transactional
  @Override
  public Long initializeNewCart() {
    Cart newCart = new Cart();
    return cartRepository.save(newCart).getId();
  }

  @Override
  public Cart getCartEntity(Long id) {
    return cartRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
  }

  @Override
  public Cart getCartByUserId(Long userId) {
    return cartRepository.findByUserId(userId);
  }
}
