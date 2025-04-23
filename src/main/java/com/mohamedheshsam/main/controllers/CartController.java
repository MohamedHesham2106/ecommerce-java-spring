package com.mohamedheshsam.main.controllers;

import java.math.BigDecimal;
import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mohamedheshsam.main.dtos.CartDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.services.cart.ICartService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/carts")
public class CartController {
  private final ICartService cartService;

  /**
   * Create a new cart.
   * POST /carts
   */
  @PostMapping
  public ResponseEntity<ApiResponse> createCart() {
    Long cartId = cartService.initializeNewCart();
    CartDto cart = cartService.getCart(cartId);

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(cartId)
        .toUri();

    return ResponseEntity
        .created(location)
        .body(new ApiResponse("Cart created successfully.", cart));
  }

  /**
   * Retrieve cart by ID.
   * GET /carts/{cartId}
   */
  @GetMapping("/{cartId}")
  public ResponseEntity<ApiResponse> getCart(@PathVariable Long cartId) {
    try {
      CartDto cart = cartService.getCart(cartId);
      return ResponseEntity.ok(new ApiResponse("Cart retrieved successfully.", cart));
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.status(404)
          .body(new ApiResponse(ex.getMessage(), null));
    }
  }

  /**
   * Get total price of items in cart.
   * GET /carts/{cartId}/total
   */
  @GetMapping("/{cartId}/total")
  public ResponseEntity<ApiResponse> getTotal(@PathVariable Long cartId) {
    try {
      BigDecimal total = cartService.getTotalPrice(cartId);
      return ResponseEntity.ok(new ApiResponse("Total price calculated successfully.", total));
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.status(404)
          .body(new ApiResponse(ex.getMessage(), null));
    }
  }

  /**
   * Clear all items from a cart.
   * DELETE /carts/{cartId}/items
   */
  @DeleteMapping("/{cartId}/items")
  public ResponseEntity<Void> clearCartItems(@PathVariable Long cartId) {
    try {
      cartService.clearCart(cartId);
      return ResponseEntity.noContent().build();
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.notFound().build();
    }
  }
}
