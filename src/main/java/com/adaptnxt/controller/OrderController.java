package com.adaptnxt.controller;

import com.adaptnxt.entity.*;
import com.adaptnxt.repository.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody OrderRequest orderRequest
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        Order order = Order.builder()
            .user(user)
            .createdAt(LocalDateTime.now())
            .street(orderRequest.getStreet())
            .city(orderRequest.getCity())
            .pincode(orderRequest.getPincode())
            .build();

        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(cartItem.getProduct())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getProduct().getPrice())
                .build();
            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);
        cartItemRepository.deleteByUser(user);

        return ResponseEntity.ok("Order placed successfully");
    }

    // DTO for order address info
    @Data
    public static class OrderRequest {
        private String street;
        private String city;
        private String pincode;
    }
}
