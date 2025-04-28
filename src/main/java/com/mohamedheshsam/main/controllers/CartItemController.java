package com.mohamedheshsam.main.controllers;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mohamedheshsam.main.requests.AddCartItemRequest;
import com.mohamedheshsam.main.requests.UpdateCartItemRequest;
import com.mohamedheshsam.main.dtos.CartDto;
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
@RequestMapping("${api.prefix}/carts/{cartId}/items")
public class CartItemController {

  private final ICartItemService cartItemService;
  private final ICartService cartService;
  private final IUserService userService;

  /**
   * Add a new item to an existing cart.
   * POST /carts/{cartId}/items
   */
  @PostMapping
  public ResponseEntity<ApiResponse> addItem(
      @PathVariable Long cartId,
      @Valid @RequestBody AddCartItemRequest request) {
    try {
      User user = userService.getAuthenticatedUser();
      Cart cart = cartService.initializeNewCart(user);
      cartItemService.addItemToCart(cart.getId(), request.getProductId(),
          request.getQuantity());
      return ResponseEntity.ok(new ApiResponse("Item added to cart successfully", null));

    } catch (ResourceNotFoundException ex) {
      return ResponseEntity
          .status(404)
          .body(new ApiResponse(ex.getMessage(), null));
    }
  }

  /**
   * Update quantity of an existing cart item.
   * PUT /carts/{cartId}/items/{itemId}
   */
  @PutMapping("/{itemId}")
  public ResponseEntity<ApiResponse> updateItem(
      @PathVariable Long cartId,
      @PathVariable Long itemId,
      @Valid @RequestBody UpdateCartItemRequest request) {
    try {
      cartItemService.updateItemQuantity(cartId, itemId, request.getQuantity());
      return ResponseEntity.ok(new ApiResponse("Update Item Success", null));

    } catch (ResourceNotFoundException ex) {
      return ResponseEntity
          .status(404)
          .body(new ApiResponse(ex.getMessage(), null));
    }
  }

  @DeleteMapping("/{itemId}")
  public ResponseEntity<ApiResponse> removeItem(
      @PathVariable Long cartId,
      @PathVariable Long itemId) {
    try {
      cartItemService.removeItemFromCart(
          cartId,
          itemId);
      return ResponseEntity
          .ok(new ApiResponse("Item removed from cart.", null));

    } catch (ResourceNotFoundException ex) {
      return ResponseEntity
          .status(404)
          .body(new ApiResponse(ex.getMessage(), null));
    }
  }
}
