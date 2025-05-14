package com.mohamedheshsam.main.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.mohamedheshsam.main.dtos.OrderDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Order;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.services.order.IOrderService;
import com.mohamedheshsam.main.services.user.IUserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {
  private final IOrderService orderService;
  private final IUserService userService;

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
}
