package com.example.inflearnjparestapi.repository;

import com.example.inflearnjparestapi.domain.Order;
import com.example.inflearnjparestapi.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;


    @Test
    void findAll() {
        orderRepository.findAll();
    }

    @Test
    void findAllByString() {
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("A");
        orderRepository.findAllByString(orderSearch);
    }

    @Test
    void findAllWithMemberDelivery() {
        orderRepository.findAllWithMemberDelivery();
    }

    @Test
    void findAllWithItem() {
        orderRepository.findAllWithItem();
    }
}      