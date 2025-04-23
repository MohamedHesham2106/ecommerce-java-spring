package com.mohamedheshsam.main.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mohamedheshsam.main.dtos.CartDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.requests.AddCartItemRequest;
import com.mohamedheshsam.main.requests.UpdateCartItemRequest;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.services.cart.ICartItemService;
import com.mohamedheshsam.main.services.cart.ICartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/carts/{cartId}/items")
public class CartItemController {
  private final ICartItemService cartItemService;
  private final ICartService cartService;

  @PostMapping
  public ResponseEntity<ApiResponse> addItemToCart(
      @PathVariable Long cartId,
      @Valid @RequestBody AddCartItemRequest request) {
    try {
      cartItemService.addItemToCart(cartId, request.getProductId(), request.getQuantity());
      CartDto updated = cartService.getCart(cartId);
      return ResponseEntity.ok(new ApiResponse("Item added to cart.", updated));
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse(ex.getMessage(), null));
    }
  }

  @PutMapping("/{productId}")
  public ResponseEntity<ApiResponse> updateItemQuantity(
      @PathVariable Long cartId,
      @PathVariable Long productId,
      @Valid @RequestBody UpdateCartItemRequest request) {
    try {
      cartItemService.updateItemQuantity(cartId, productId, request.getQuantity());
      CartDto updated = cartService.getCart(cartId);
      return ResponseEntity.ok(new ApiResponse("Item quantity updated.", updated));
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse(ex.getMessage(), null));
    }
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<ApiResponse> removeItem(
      @PathVariable Long cartId,
      @PathVariable Long productId,
      @RequestParam(value = "quantity", required = false) Integer quantity) {
    try {
      CartDto updated = cartItemService.removeItemFromCart(cartId, productId, quantity);
      return ResponseEntity.ok(new ApiResponse("Cart updated successfully.", updated));
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse(ex.getMessage(), null));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ApiResponse(ex.getMessage(), null));
    }
  }

}
