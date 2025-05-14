package com.mohamedheshsam.main.services.order;

import java.util.List;

import com.mohamedheshsam.main.models.Order;
import com.mohamedheshsam.main.dtos.OrderDto;

public interface IOrderService {
  Order placeOrder(Long userId);

  Boolean cancelOrder(Long orderId);

  Boolean updateOrderStatus(Long orderId, String status);

  OrderDto getOrderById(Long orderId);

  List<OrderDto> getUserOrders(Long userId);

  OrderDto convertToDto(Order order);
}
