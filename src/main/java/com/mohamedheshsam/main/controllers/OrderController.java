package com.mohamedheshsam.main.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.mohamedheshsam.main.dtos.OrderDto;
import com.mohamedheshsam.main.enums.OrderStatus;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Order;
import com.mohamedheshsam.main.requests.UpdateOrderStatusRequest;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.services.order.IOrderService;
import com.mohamedheshsam.main.services.user.IUserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {
  private final IOrderService orderService;
  private final IUserService userService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/all")
  public ResponseEntity<ApiResponse> getAllOrders() {
    try {
      List<OrderDto> orders = orderService.getAllOrders();
      return ResponseEntity.ok(new ApiResponse("All orders retrieved successfully", orders));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse("No orders found", e.getMessage()));
    }
  }

  @PostMapping
  public ResponseEntity<ApiResponse> createOrder() {
    try {
      Long userId = userService.getAuthenticatedUser().getId();
      Order order = orderService.placeOrder(userId);
      OrderDto orderDto = orderService.convertToDto(order);
      return ResponseEntity.ok(new ApiResponse("Items Order Success!", orderDto));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Error occurred!", e.getMessage()));
    }
  }

  @GetMapping
  public ResponseEntity<ApiResponse> getUserOrders() {
    try {
      Long userId = userService.getAuthenticatedUser().getId();
      List<OrderDto> orders = orderService.getUserOrders(userId);
      return ResponseEntity.ok(new ApiResponse("User orders retrieved successfully", orders));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse("No orders found for user", e.getMessage()));
    }
  }

  @DeleteMapping("/{id}/cancel")
  public ResponseEntity<ApiResponse> cancelOrder(@PathVariable Long id) {
    try {
      orderService.cancelOrder(id);
      return ResponseEntity.ok(new ApiResponse("Order cancelled successfully", null));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse("Order not found", e.getMessage()));
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse> updateOrderStatus(@PathVariable Long id,
      @RequestBody UpdateOrderStatusRequest status) {
    try {
      orderService.updateOrderStatus(id, status);
      return ResponseEntity.ok(new ApiResponse("Order status updated successfully", null));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse("Order not found", e.getMessage()));
    }
  }

}
