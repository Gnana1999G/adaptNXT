package com.adaptnxt.repository;

import com.adaptnxt.entity.Order;
import com.adaptnxt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
