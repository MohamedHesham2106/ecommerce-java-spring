package com.mohamedheshsam.main.services.cart;

import java.math.BigDecimal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.mohamedheshsam.main.dtos.CartDto;
import com.mohamedheshsam.main.dtos.CartItemDto;
import com.mohamedheshsam.main.dtos.ImageDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Cart;
import com.mohamedheshsam.main.models.CartItem;
import com.mohamedheshsam.main.models.Image;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.respository.CartItemRepository;
import com.mohamedheshsam.main.respository.CartRepository;
import com.mohamedheshsam.main.respository.ImageRepository;
import com.mohamedheshsam.main.services.products.IProductService;
import com.mohamedheshsam.main.exceptions.ImageConversionException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartItemService implements ICartItemService {
  private final CartItemRepository cartItemRepository;
  private final CartRepository cartRepository;
  private final IProductService productService;
  private final ICartService cartService;
  private final ImageRepository imageRepository;
  private final ModelMapper modelMapper;

  @Override
  public void addItemToCart(Long cartId, Long productId, int quantity) {
    Cart cart = cartService.getCart(cartId);
    Product product = productService.getProductById(productId);
    CartItem cartItem = cart.getItems()
        .stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .findFirst().orElse(new CartItem());
    if (cartItem.getId() == null) {
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(quantity);
      cartItem.setUnitPrice(product.getPrice());
    } else {
      cartItem.setQuantity(cartItem.getQuantity() + quantity);
    }
    cartItem.setTotalPrice();
    cart.addItem(cartItem);
    cartItemRepository.save(cartItem);
    cartRepository.save(cart);
  }

  @Override
  public void removeItemFromCart(Long cartId, Long productId) {
    Cart cart = cartService.getCart(cartId);
    CartItem itemToRemove = getCartItem(cartId, productId);
    cart.removeItem(itemToRemove);
    cartRepository.save(cart);
  }

  @Override
  public void updateItemQuantity(Long cartId, Long productId, int quantity) {
    Cart cart = cartService.getCart(cartId);
    cart.getItems()
        .stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .findFirst()
        .ifPresent(item -> {
          item.setQuantity(quantity);
          item.setUnitPrice(item.getProduct().getPrice());
          item.setTotalPrice();
        });
    BigDecimal totalAmount = cart.getItems()
        .stream().map(CartItem::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    cart.setTotalAmount(totalAmount);
    cartRepository.save(cart);
  }

  @Override
  public CartItem getCartItem(Long cartId, Long productId) {
    Cart cart = cartService.getCart(cartId);
    return cart.getItems()
        .stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .findFirst().orElseThrow(() -> new ResourceNotFoundException("Item not found"));
  }

  @Override
  public CartItemDto convertToDto(CartItem cartItem) {
    CartItemDto cartItemDto = modelMapper.map(cartItem, CartItemDto.class);
    List<Image> images = imageRepository.findByProductId(cartItem.getProduct().getId());
    cartItemDto.setImageUrls(images.stream().map(image -> {
      ImageDto imageDto = new ImageDto();
      imageDto.setId(image.getId());
      imageDto.setFileName(image.getFileName());
      imageDto.setImageUrl(image.getImageUrl());
      return imageDto;
    }).toList());
    return cartItemDto;
  }
}
