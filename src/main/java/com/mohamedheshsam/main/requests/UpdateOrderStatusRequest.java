package com.mohamedheshsam.main.requests;

import com.mohamedheshsam.main.enums.OrderStatus;

public class UpdateOrderStatusRequest {
  private OrderStatus status;

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

}
