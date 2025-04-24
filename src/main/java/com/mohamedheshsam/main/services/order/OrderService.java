package com.mohamedheshsam.main.services.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.mohamedheshsam.main.dtos.OrderDto;
import com.mohamedheshsam.main.enums.OrderStatus;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Cart;
import com.mohamedheshsam.main.models.Order;
import com.mohamedheshsam.main.models.OrderItem;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.respository.OrderRepository;
import com.mohamedheshsam.main.respository.ProductRepository;
import com.mohamedheshsam.main.services.cart.CartService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final CartService cartService;
  private final ModelMapper modelMapper;

  @Transactional
  @Override
  public Order placeOrder(Long userId) {
    Cart cart = cartService.getCartByUserId(userId);
    Order order = createOrder(cart);
    List<OrderItem> orderItemList = createOrderItems(order, cart);
    order.setOrderItems(new HashSet<>(orderItemList));
    order.setTotalAmount(calculateTotalAmount(orderItemList));
    Order savedOrder = orderRepository.save(order);
    cartService.clearCart(cart.getId());
    return savedOrder;
  }

  private List<OrderItem> createOrderItems(Order order, Cart cart) {
    return cart.getItems().stream().map(cartItem -> {
      Product product = cartItem.getProduct();
      product.setInventory(product.getInventory() - cartItem.getQuantity());
      productRepository.save(product);
      return new OrderItem(
          order,
          product,
          cartItem.getQuantity(),
          cartItem.getUnitPrice());
    }).toList();

  }

  private Order createOrder(Cart cart) {
    Order order = new Order();
    order.setUser(cart.getUser());
    order.setStatus(OrderStatus.PENDING);
    order.setOrderDate(LocalDate.now());
    return order;
  }

  private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
    return orderItems
        .stream()
        .map(item -> item.getPrice()
            .multiply(new BigDecimal(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

  }

  @Override
  public OrderDto getOrderById(Long orderId) {
    return orderRepository.findById(orderId)
        .map(this::convertToDto)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
  }

  @Override
  public List<OrderDto> getUserOrders(Long userId) {
    List<Order> orders = orderRepository.findByUserId(userId);
    return orders.stream().map(this::convertToDto).toList();
  }

  @Override
  public OrderDto convertToDto(Order order) {
    return modelMapper.map(order, OrderDto.class);
  }
}
