package com.adaptnxt.controller;

import com.adaptnxt.entity.*;
import com.adaptnxt.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Get current user's cart items
    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(cartItemRepository.findByUser(user));
    }

    // Add item to cart
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam Long productId,
        @RequestParam int quantity
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        Optional<CartItem> existing = cartItemRepository.findByUserAndProduct(user, product);
        CartItem cartItem;

        if (existing.isPresent()) {
            cartItem = existing.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = CartItem.builder()
                .user(user)
                .product(product)
                .quantity(quantity)
                .build();
        }

        cartItemRepository.save(cartItem);
        return ResponseEntity.ok(cartItem);
    }

    // Update item quantity
    @PutMapping("/update")
    public ResponseEntity<?> updateCartItem(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam Long cartItemId,
        @RequestParam int quantity
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow();
        if (!cartItem.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return ResponseEntity.ok(cartItem);
    }

    // Remove item from cart
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam Long cartItemId
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow();
        if (!cartItem.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        cartItemRepository.delete(cartItem);
        return ResponseEntity.ok("Removed");
    }
}
