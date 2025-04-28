package com.mohamedheshsam.main.services.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.mohamedheshsam.main.dtos.CartDto;
import com.mohamedheshsam.main.dtos.CartItemDto;
import com.mohamedheshsam.main.dtos.ProductDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Cart;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.respository.CartItemRepository;
import com.mohamedheshsam.main.respository.CartRepository;
import com.mohamedheshsam.main.services.products.IProductService;
import com.mohamedheshsam.main.services.user.IUserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final ModelMapper modelMapper;
  private final IUserService userService;
  private final IProductService productService;

  @Override
  public Cart getCart(Long id) {
    Cart cart = cartRepository.findById(
        id)
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    BigDecimal totalAmount = cart.getTotalAmount();
    cart.setTotalAmount(totalAmount);
    return cartRepository.save(cart);
  }

  @Transactional
  @Override
  public void clearCart(Long id) {
    Cart cart = getCart(id);
    cartItemRepository.deleteAllByCartId(id);
    cart.clearCart();
    cartRepository.deleteById(id);
  }

  @Override
  public BigDecimal getTotalPrice(Long id) {
    Cart cart = cartRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    return cart.getTotalAmount();
  }

  @Override
  public Cart initializeNewCart(User user) {
    return Optional.ofNullable(getCartByUserId(user.getId()))
        .orElseGet(() -> {
          Cart cart = new Cart();
          cart.setUser(user);
          return cartRepository.save(cart);
        });
  }

  @Override
  public Cart getCartByUserId(Long userId) {
    return cartRepository.findByUserId(userId);
  }

  @Override
  public CartDto convertToDto(Cart cart) {
    return modelMapper.map(cart, CartDto.class);
  }
}
