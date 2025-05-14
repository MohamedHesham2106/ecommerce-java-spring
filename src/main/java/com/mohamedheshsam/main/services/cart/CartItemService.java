package com.mohamedheshsam.main.services.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.mohamedheshsam.main.dtos.CartDto;
import com.mohamedheshsam.main.dtos.CartItemDto;
import com.mohamedheshsam.main.dtos.ImageDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Cart;
import com.mohamedheshsam.main.models.CartItem;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.respository.CartItemRepository;
import com.mohamedheshsam.main.respository.ImageRepository;
import com.mohamedheshsam.main.services.products.IProductService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartItemService implements ICartItemService {
  private final CartItemRepository cartItemRepository;
  private final IProductService productService;
  private final ICartService cartService;
  private final ModelMapper modelMapper;

  @Override
  public void addItemToCart(Long cartId, Long productId, int quantity) {
    Cart cart = cartService.getCart(cartId);
    Product product = productService.getProductById(productId);
    CartItem cartItem = cart.getItems().stream()
        .filter(i -> i.getProduct().getId().equals(productId))
        .findFirst()
        .orElseGet(() -> new CartItem(cart, product, 0, product.getPrice(), BigDecimal.ZERO));
    cartItem.setQuantity(cartItem.getQuantity() + quantity);
    cartItem.setUnitPrice(product.getPrice());
    cartItem.setTotalPrice();
    cartItemRepository.save(cartItem);
    recalcCartTotal(cart);
  }

  @Override
  public void removeItemFromCart(Long cartId, Long productId) {
    Cart cart = cartService.getCart(cartId);
    CartItem item = cart.getItems().stream()
        .filter(i -> i.getProduct().getId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Item not found."));
    cartItemRepository.delete(item);
    cart.getItems().remove(item);
    recalcCartTotal(cart);
    if (cart.getItems().isEmpty()) {
      cartService.deleteCart(cart.getId());
    }
  }

  @Override
  public void updateItemQuantity(Long cartId, Long productId, int quantity) {
    CartItem item = cartService.getCart(cartId).getItems().stream()
        .filter(i -> i.getProduct().getId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Item not found."));
    item.setQuantity(quantity);
    item.setUnitPrice(item.getProduct().getPrice());
    item.setTotalPrice();
    cartItemRepository.save(item);
    recalcCartTotal(item.getCart());
  }

  @Override
  public CartItem getCartItem(Long cartId, Long productId) {
    return cartService.getCart(cartId).getItems().stream()
        .filter(i -> i.getProduct().getId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Item not found."));
  }

  @Override
  public CartItemDto convertToDto(CartItem cartItem) {
    CartItemDto dto = modelMapper.map(cartItem, CartItemDto.class);
    return dto;
  }

  private void recalcCartTotal(Cart cart) {
    BigDecimal total = cart.getItems().stream()
        .map(CartItem::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    cart.setTotalAmount(total);
    cartService.convertToDto(cart); // or save via repository in service
  }
}
