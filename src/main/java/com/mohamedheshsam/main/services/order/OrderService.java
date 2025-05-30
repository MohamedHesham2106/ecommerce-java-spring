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
import com.mohamedheshsam.main.requests.UpdateOrderStatusRequest;
import com.mohamedheshsam.main.respository.CartRepository;
import com.mohamedheshsam.main.respository.OrderRepository;
import com.mohamedheshsam.main.respository.ProductRepository;
import com.mohamedheshsam.main.services.cart.CartService;
import com.mohamedheshsam.main.respository.ImageRepository;
import com.mohamedheshsam.main.dtos.OrderItemDto;
import com.mohamedheshsam.main.dtos.ImageDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final CartService cartService;
  private final ModelMapper modelMapper;
  private final ImageRepository imageRepository;

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
    OrderDto orderDto = modelMapper.map(order, OrderDto.class);
    if (orderDto.getItems() != null) {
      for (OrderItemDto itemDto : orderDto.getItems()) {
        if (itemDto.getId() != null) {
          // Find the matching OrderItem in the order
          OrderItem orderItem = order.getOrderItems().stream()
              .filter(oi -> oi.getId().equals(itemDto.getId()))
              .findFirst().orElse(null);
          if (orderItem != null && orderItem.getProduct() != null) {
            itemDto.setName(orderItem.getProduct().getName());
            // Set images
            var images = imageRepository.findByProductId(orderItem.getProduct().getId());
            List<ImageDto> imageDtos = images.stream().map(img -> modelMapper.map(img, ImageDto.class)).toList();
            itemDto.setImages(imageDtos);
          }
        }
      }
    }
    return orderDto;
  }

  @Override
  public Boolean cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    if (order.getOrderItems() != null) {
      order.getOrderItems().forEach(orderItem -> {
        Product product = orderItem.getProduct();
        if (product != null) {
          product.setInventory(product.getInventory() + orderItem.getQuantity());
          productRepository.save(product);
        }
      });
    }
    order.setStatus(OrderStatus.CANCELLED);
    orderRepository.save(order);
    return true;
  }

  @Override
  public Boolean updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

    order.setStatus(request.getStatus());
    orderRepository.save(order);
    return true;
  }

  @Override
  public List<OrderDto> getAllOrders() {
    List<Order> orders = orderRepository.findAll();
    return orders.stream().map(this::convertToDto).toList();
  }
}
