package com.example.inflearnjparestapi.repository.querydsl;

import com.example.inflearnjparestapi.domain.OrderStatus;
import com.example.inflearnjparestapi.repository.OrderSearch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderQuerydslRepositoryTest {

    @Autowired
    OrderQuerydslRepository orderQuerydslRepository;

    @Test
    void findAll() {
        orderQuerydslRepository.findAll();
    }

    @Test
    void findAllByString() {
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("A");
        orderQuerydslRepository.findAllByString(orderSearch);
    }

    @Test
    void findAllWithMemberDelivery(){
        orderQuerydslRepository.findAllWithMemberDelivery();
    }

    @Test
    void findAllWithItem(){
        orderQuerydslRepository.findAllWithItem();
    }
}