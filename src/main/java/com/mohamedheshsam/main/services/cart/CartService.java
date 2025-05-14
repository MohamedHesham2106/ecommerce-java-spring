package com.mohamedheshsam.main.services.cart;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.mohamedheshsam.main.dtos.CartDto;
import com.mohamedheshsam.main.dtos.CartItemDto;
import com.mohamedheshsam.main.dtos.ProductDto;
import com.mohamedheshsam.main.dtos.ImageDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Cart;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.respository.CartItemRepository;
import com.mohamedheshsam.main.respository.CartRepository;
import com.mohamedheshsam.main.respository.ImageRepository;
import com.mohamedheshsam.main.services.products.IProductService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
  private final CartRepository cartRepository;
  private final IProductService productService;

  @Override
  public Cart initializeNewCart(User user) {
    return Optional.ofNullable(cartRepository.findByUserId(user.getId()))
        .orElseGet(() -> cartRepository.save(new Cart(user, BigDecimal.ZERO)));
  }

  @Override
  public Cart getCart(Long id) {
    return cartRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found."));
  }

  @Transactional
  @Override
  public void clearCart(Long id) {
    Cart cart = getCart(id);
    cart.getItems().clear();
    cart.setTotalAmount(BigDecimal.ZERO);
    cartRepository.save(cart);
  }

  @Override
  public Cart getCartByUserId(Long userId) {
    return Optional.ofNullable(cartRepository.findByUserId(userId))
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user."));
  }

  @Override
  public CartDto convertToDto(Cart cart) {
    CartDto dto = new CartDto();
    dto.setId(cart.getId());
    // Map items with product and images
    List<CartItemDto> itemDtos = cart.getItems().stream().map(item -> {
      CartItemDto itemDto = new CartItemDto();
      itemDto.setId(item.getId());
      itemDto.setQuantity(item.getQuantity());
      itemDto.setUnitPrice(item.getUnitPrice());
      itemDto.setTotalPrice(item.getTotalPrice());
      // Set product details
      Product product = item.getProduct();
      if (product != null) {
        ProductDto productDto = productService.convertToDto(product);
        itemDto.setProduct(productDto);
      } else {
        itemDto.setProduct(null);
      }
      return itemDto;
    }).toList();
    dto.setItems(itemDtos);
    dto.setTotalAmount(itemDtos.stream()
        .map(CartItemDto::getTotalPrice)
        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
    return dto;
  }

  @Override
  public BigDecimal getItemsCount(Long id) {
    Cart cart = getCart(id);
    return BigDecimal.valueOf(cart.getItems().size());
  }

  @Override
  public void deleteCart(Long id) {
    cartRepository.deleteById(id);
  }
}
