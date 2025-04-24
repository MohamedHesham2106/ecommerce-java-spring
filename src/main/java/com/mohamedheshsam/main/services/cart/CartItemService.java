package com.mohamedheshsam.main.services.cart;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.mohamedheshsam.main.dtos.CartDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Cart;
import com.mohamedheshsam.main.models.CartItem;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.respository.CartItemRepository;
import com.mohamedheshsam.main.respository.CartRepository;
import com.mohamedheshsam.main.services.products.IProductService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartItemService implements ICartItemService {
  private final CartItemRepository cartItemRepository;
  private final CartRepository cartRepository;
  private final IProductService productService;
  private final ICartService cartService;

  @Override
  public void addItemToCart(Long cartId, Long productId, int quantity) {
    Cart cart = cartService.getCartEntity(cartId);
    Product product = productService.getProductById(productId);

    CartItem cartItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .findFirst()
        .orElse(new CartItem());

    if (cartItem.getId() == null) {
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(quantity);
      cartItem.setUnitPrice(product.getPrice());
    } else {
      cartItem.setQuantity(cartItem.getQuantity() + quantity);
    }

    cartItem.setTotalPrice();
    cart.addItem(cartItem);

    cartItemRepository.save(cartItem);
    cartRepository.save(cart);
  }

  @Override
  public CartDto removeItemFromCart(Long cartId, Long productId, Integer quantity) {
    Cart cart = cartService.getCartEntity(cartId);
    CartItem item = getCartItem(cartId, productId);

    int toRemove = (quantity == null ? 1 : quantity);
    if (toRemove < item.getQuantity()) {
      item.setQuantity(item.getQuantity() - toRemove);
      item.setTotalPrice();
      cartItemRepository.save(item);
    } else {
      cart.removeItem(item);
      cartItemRepository.delete(item);
    }

    BigDecimal totalAmount = cart.getItems().stream()
        .map(CartItem::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    cart.setTotalAmount(totalAmount);
    cartRepository.save(cart);

    return cartService.getCart(cartId);
  }

  @Override
  public void updateItemQuantity(Long cartId, Long productId, int quantity) {
    // Fetch the cart
    Cart cart = cartService.getCartEntity(cartId);

    // Update the quantity of the specified CartItem
    cart.getItems()
        .stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .findFirst()
        .ifPresent(item -> {
          item.setQuantity(quantity);
          item.setUnitPrice(item.getProduct().getPrice());
          item.setTotalPrice();
        });

    // Recalculate the total amount of the cart
    BigDecimal totalAmount = cart.getItems()
        .stream()
        .map(CartItem::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    cart.setTotalAmount(totalAmount);

    // Save the updated cart
    cartRepository.save(cart);
  }

  @Override
  public CartItem getCartItem(Long cartId, Long productId) {
    // Fetch the cart and find the specified CartItem
    Cart cart = cartService.getCartEntity(cartId); // Use getCartEntity
    return cart.getItems()
        .stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Item not found in the cart"));
  }
}
