package com.bogecom.cart.service;

import com.bogecom.cart.dto.AddToCartRequest;
import com.bogecom.cart.dto.CartDto;
import com.bogecom.cart.dto.UpdateCartItemRequest;
import com.bogecom.cart.entity.Cart;
import com.bogecom.cart.entity.CartItem;
import com.bogecom.cart.entity.CartStatus;
import com.bogecom.cart.mapper.CartMapper;
import com.bogecom.cart.repository.CartRepository;
import com.bogecom.exception.BusinessException;
import com.bogecom.product.entity.Product;
import com.bogecom.product.repository.ProductRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final CartMapper cartMapper;

  @Transactional(readOnly = true)
  public CartDto getCart(Long userId, String sessionId) {
    return cartMapper.toDto(getOrCreateCartEntity(userId, sessionId));
  }

  @Transactional
  public CartDto addToCart(Long userId, AddToCartRequest request) {
    Cart cart = getOrCreateCartEntity(userId, request.sessionId());

    Product product =
        productRepository
            .findById(request.productId())
            .filter(p -> !p.isDeleted())
            .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

    if (!product.isPublished()) {
      throw new BusinessException("Product is not available for purchase", HttpStatus.BAD_REQUEST);
    }

    // Check if item already in cart
    Optional<CartItem> existingItemOpt =
        cart.getItems().stream()
            .filter(item -> item.getProductId().equals(product.getId()))
            .findFirst();

    if (existingItemOpt.isPresent()) {
      CartItem existingItem = existingItemOpt.get();
      existingItem.setQuantity(existingItem.getQuantity() + request.quantity());
      log.info("Updated quantity of product {} in cart {}", product.getId(), cart.getId());
    } else {
      CartItem newItem =
          CartItem.builder()
              .productId(product.getId())
              .quantity(request.quantity())
              .priceAtAdded(product.getPrice()) // snapshot current price
              .build();
      cart.addItem(newItem);
      log.info("Added product {} to cart {}", product.getId(), cart.getId());
    }

    cart = cartRepository.save(cart);
    return cartMapper.toDto(cart);
  }

  @Transactional
  public CartDto updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request) {
    Cart cart = getOrCreateCartEntity(userId, request.sessionId());

    CartItem itemToUpdate =
        cart.getItems().stream()
            .filter(item -> item.getId().equals(itemId))
            .findFirst()
            .orElseThrow(
                () -> new BusinessException("Item not found in cart", HttpStatus.NOT_FOUND));

    itemToUpdate.setQuantity(request.quantity());
    cartRepository.save(cart);

    log.info("Updated quantity of item {} to {}", itemId, request.quantity());
    return cartMapper.toDto(cart);
  }

  @Transactional
  public CartDto removeCartItem(Long userId, String sessionId, Long itemId) {
    Cart cart = getOrCreateCartEntity(userId, sessionId);

    CartItem itemToRemove =
        cart.getItems().stream()
            .filter(item -> item.getId().equals(itemId))
            .findFirst()
            .orElseThrow(
                () -> new BusinessException("Item not found in cart", HttpStatus.NOT_FOUND));

    cart.removeItem(itemToRemove);
    cartRepository.save(cart);

    log.info("Removed item {} from cart {}", itemId, cart.getId());
    return cartMapper.toDto(cart);
  }

  @Transactional
  public void clearCart(Long userId, String sessionId) {
    Cart cart = getOrCreateCartEntity(userId, sessionId);
    cart.getItems().clear();
    cartRepository.save(cart);
    log.info("Cleared cart {}", cart.getId());
  }

  @Transactional
  public void convertCart(Long userId, String sessionId) {
    Cart cart = getOrCreateCartEntity(userId, sessionId);
    cart.setStatus(CartStatus.CONVERTED);
    cartRepository.save(cart);
    log.info("Cart {} marked as converted", cart.getId());
  }

  @Transactional
  public void mergeGuestCartWithUserCart(Long userId, String sessionId) {
    if (sessionId == null) {
      return;
    }

    Optional<Cart> guestCartOpt =
        cartRepository.findBySessionIdAndStatusAndIsDeletedFalse(sessionId, CartStatus.ACTIVE);
    if (guestCartOpt.isEmpty() || guestCartOpt.get().getItems().isEmpty()) {
      return; // Nothing to merge
    }

    Cart guestCart = guestCartOpt.get();
    Cart userCart = getOrCreateCartEntity(userId, null);

    if (userCart.getId().equals(guestCart.getId())) {
      return; // Already same cart
    }

    // Move items from guest cart to user cart
    for (CartItem guestItem : guestCart.getItems()) {
      Optional<CartItem> existingUserItem =
          userCart.getItems().stream()
              .filter(item -> item.getProductId().equals(guestItem.getProductId()))
              .findFirst();

      if (existingUserItem.isPresent()) {
        existingUserItem
            .get()
            .setQuantity(existingUserItem.get().getQuantity() + guestItem.getQuantity());
      } else {
        CartItem newItem =
            CartItem.builder()
                .productId(guestItem.getProductId())
                .quantity(guestItem.getQuantity())
                .priceAtAdded(guestItem.getPriceAtAdded())
                .build();
        userCart.addItem(newItem);
      }
    }

    guestCart.setStatus(CartStatus.ABANDONED);
    cartRepository.save(guestCart);
    cartRepository.save(userCart);

    log.info("Merged guest cart {} into user cart {}", guestCart.getId(), userCart.getId());
  }

  // --- Private Helpers ---

  private Cart getOrCreateCartEntity(Long userId, String sessionId) {
    if (userId != null) {
      return cartRepository
          .findByUserIdAndStatusAndIsDeletedFalse(userId, CartStatus.ACTIVE)
          .orElseGet(() -> createNewCart(userId, null));
    } else if (sessionId != null) {
      return cartRepository
          .findBySessionIdAndStatusAndIsDeletedFalse(sessionId, CartStatus.ACTIVE)
          .orElseGet(() -> createNewCart(null, sessionId));
    } else {
      throw new BusinessException(
          "Either userId or sessionId must be provided", HttpStatus.BAD_REQUEST);
    }
  }

  private Cart createNewCart(Long userId, String sessionId) {
    Cart cart = new Cart();
    cart.setUserId(userId);
    cart.setSessionId(sessionId);
    cart.setStatus(CartStatus.ACTIVE);
    return cartRepository.save(cart);
  }
}
