package com.mohamedheshsam.main.controllers;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mohamedheshsam.main.dtos.CartDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.services.cart.ICartService;
import com.mohamedheshsam.main.services.user.IUserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/carts")
public class CartController {
  private final ICartService cartService;
  private final IUserService userService;

  /**
   * Retrieve a user's cart (single active cart per user).
   * GET /carts/users/{userId}
   */
  @GetMapping()
  public ResponseEntity<ApiResponse> getUserCart() {
    try {
      Long userId = userService.getAuthenticatedUser().getId();
      CartDto dto = cartService.convertToDto(cartService.getCartByUserId(userId));
      return ResponseEntity.ok(new ApiResponse("Cart retrieved successfully.", dto));
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse(ex.getMessage(), null));
    }
  }

  /**
   * Clear all items from an existing cart.
   * DELETE /carts/{cartId}
   */
  @DeleteMapping
  public ResponseEntity<ApiResponse> clearCart() {
    try {
      Long userId = userService.getAuthenticatedUser().getId();
      Long cartId = cartService.getCartByUserId(userId).getId();
      cartService.clearCart(cartId);
      return ResponseEntity.ok(new ApiResponse("Cart cleared successfully.", null));
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse(ex.getMessage(), null));
    }
  }

  /**
   * Get the number of items in a cart.
   * GET /carts/{cartId}/items/count
   */
  @GetMapping("/count")
  public ResponseEntity<ApiResponse> getItemsCount() {
    try {
      Long userId = userService.getAuthenticatedUser().getId();
      Long cartId = cartService.getCartByUserId(userId).getId();
      BigDecimal count = cartService.getItemsCount(cartId);
      HashMap<String, BigDecimal> response = new HashMap<>();
      response.put("count", count);
      return ResponseEntity.ok(new ApiResponse("Items count retrieved successfully.", response));
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse(ex.getMessage(), null));
    }
  }
}
