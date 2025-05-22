package com.mohamedheshsam.main.services.order;

import java.util.List;

import com.mohamedheshsam.main.models.Order;
import com.mohamedheshsam.main.requests.UpdateOrderStatusRequest;
import com.mohamedheshsam.main.dtos.OrderDto;
import com.mohamedheshsam.main.enums.OrderStatus;

public interface IOrderService {
  Order placeOrder(Long userId);

  List<OrderDto> getAllOrders();

  Boolean cancelOrder(Long orderId);

  Boolean updateOrderStatus(Long orderId, UpdateOrderStatusRequest status);

  OrderDto getOrderById(Long orderId);

  List<OrderDto> getUserOrders(Long userId);

  OrderDto convertToDto(Order order);
}
