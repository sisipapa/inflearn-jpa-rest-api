package com.example.inflearnjparestapi.repository.jpa;

import com.example.inflearnjparestapi.domain.Order;
import com.example.inflearnjparestapi.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}
