package com.mohamedheshsam.main.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mohamedheshsam.main.models.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByUserId(Long userId);
}
