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
    // Fetch the cart and product
    Cart cart = cartService.getCartEntity(cartId); // Fetch the Cart entity
    Product product = productService.getProductById(productId);

    // Check if the product has enough inventory
    if (product.getInventory() < quantity) {
      throw new IllegalArgumentException("Not enough inventory for product: " + product.getName());
    }

    // Check if the product already exists in the cart
    CartItem cartItem = cart.getItems()
        .stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .findFirst()
        .orElse(new CartItem());

    if (cartItem.getId() == null) {
      // Create a new CartItem
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(quantity);
      cartItem.setUnitPrice(product.getPrice());
    } else {
      // Update the quantity of the existing CartItem
      cartItem.setQuantity(cartItem.getQuantity() + quantity);
    }

    cartItem.setTotalPrice();

    product.setInventory(product.getInventory() - quantity);

    // Save the updated entities
    cart.addItem(cartItem);
    cartItemRepository.save(cartItem);
    cartRepository.save(cart);
    productService.saveProduct(product);
  }

  @Override
  public CartDto removeItemFromCart(Long cartId, Long productId, Integer quantity) {
    // Fetch the cart and the item to remove
    Cart cart = cartService.getCartEntity(cartId);
    CartItem item = getCartItem(cartId, productId);

    // Determine the quantity to remove (default to 1 if not provided)
    int toRemove = (quantity == null ? 1 : quantity);

    if (toRemove < item.getQuantity()) {
      // Decrease the quantity of the CartItem
      item.setQuantity(item.getQuantity() - toRemove);
      item.setTotalPrice();

      // Increase the product's inventory
      Product product = item.getProduct();
      product.setInventory(product.getInventory() + toRemove);
      productService.saveProduct(product);

      // Save the updated CartItem
      cartItemRepository.save(item);
    } else {
      // Remove the CartItem entirely
      cart.removeItem(item);

      // Increase the product's inventory by the full quantity of the CartItem
      Product product = item.getProduct();
      product.setInventory(product.getInventory() + item.getQuantity());
      productService.saveProduct(product);

      // Delete the CartItem
      cartItemRepository.delete(item);
    }

    // Recalculate the total amount of the cart
    BigDecimal totalAmount = cart.getItems().stream()
        .map(CartItem::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    cart.setTotalAmount(totalAmount);

    // Save the updated cart
    cartRepository.save(cart);

    // Return the updated cart as a DTO
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
