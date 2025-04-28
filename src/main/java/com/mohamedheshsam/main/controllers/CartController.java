package com.mohamedheshsam.main.controllers;

import java.math.BigDecimal;
import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mohamedheshsam.main.dtos.CartDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Cart;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.services.cart.ICartService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/carts")
public class CartController {
  private final ICartService cartService;

  @GetMapping("/users/{userId}")
  public ResponseEntity<ApiResponse> getUserCart(@PathVariable Long userId) {
    try {
      Cart cart = cartService.getCartByUserId(userId);
      CartDto cartDto = cartService.convertToDto(cart);
      return ResponseEntity.ok(new ApiResponse("Success", cartDto));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    }
  }

  @DeleteMapping("/{cartId}")
  public ResponseEntity<ApiResponse> clearCart(@PathVariable Long cartId) {
    try {
      cartService.clearCart(cartId);
      return ResponseEntity.ok(new ApiResponse("Clear Cart Success!", null));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    }
  }

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

}
