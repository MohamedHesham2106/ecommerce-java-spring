package com.mohamedheshsam.main.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mohamedheshsam.main.models.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
  Cart findByUserId(Long userId);

}
