package com.mohamedheshsam.main.services.order;

import java.util.List;

import com.mohamedheshsam.main.models.Order;
import com.mohamedheshsam.main.dtos.OrderDto;

public interface IOrderService {
  Order placeOrder(Long userId);

  OrderDto getOrderById(Long orderId);

  List<OrderDto> getUserOrders(Long userId);
}
