package com.mohamedheshsam.main.controllers;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mohamedheshsam.main.requests.AddCartItemRequest;
import com.mohamedheshsam.main.requests.UpdateCartItemRequest;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Cart;
import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.services.cart.ICartItemService;
import com.mohamedheshsam.main.services.cart.ICartService;
import com.mohamedheshsam.main.services.user.IUserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/carts/items")
public class CartItemController {
  private final ICartItemService cartItemService;
  private final ICartService cartService;
  private final IUserService userService;

  /**
   * Initialize or fetch the authenticated user's cart (max one), then add or
   * merge an item.
   * POST /carts/items
   */
  @PostMapping
  public ResponseEntity<ApiResponse> addItem(@Valid @RequestBody AddCartItemRequest request) {
    User user = userService.getAuthenticatedUser();
    Cart cart;
    try {
      cart = cartService.getCartByUserId(user.getId());
    } catch (ResourceNotFoundException ex) {
      cart = cartService.initializeNewCart(user);
    }
    cartItemService.addItemToCart(cart.getId(), request.getProductId(), request.getQuantity());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse("Item added successfully.", cart.getId()));
  }

  /**
   * Update quantity for a product in the authenticated user's cart.
   * PUT /carts/items/{productId}
   */
  @PutMapping("/{productId}")
  public ResponseEntity<ApiResponse> updateItem(
      @PathVariable Long productId,
      @Valid @RequestBody UpdateCartItemRequest request) {
    User user = userService.getAuthenticatedUser();
    Cart cart = cartService.getCartByUserId(user.getId());
    try {
      cartItemService.updateItemQuantity(cart.getId(), productId, request.getQuantity());
      return ResponseEntity.ok(new ApiResponse("Item updated successfully.", null));
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse(ex.getMessage(), null));
    }
  }

  /**
   * Remove a product from the authenticated user's cart.
   * DELETE /carts/items/{productId}
   */
  @DeleteMapping("/{productId}")
  public ResponseEntity<ApiResponse> removeItem(@PathVariable Long productId) {
    User user = userService.getAuthenticatedUser();
    Cart cart = cartService.getCartByUserId(user.getId());
    try {
      cartItemService.removeItemFromCart(cart.getId(), productId);
      return ResponseEntity.ok(new ApiResponse("Item removed successfully.", null));
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse(ex.getMessage(), null));
    }
  }
}
