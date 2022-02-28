package com.example.inflearnjparestapi.repository.jpa;

import com.example.inflearnjparestapi.InitDb;
import com.example.inflearnjparestapi.domain.Member;
import com.example.inflearnjparestapi.domain.Order;
import com.example.inflearnjparestapi.domain.OrderItem;
import com.example.inflearnjparestapi.domain.item.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(InitDb.class)
class OrderItemJpaRepositoryTest {

    @Autowired
    OrderItemJpaRepository orderItemJpaRepository;

    @Test
    void test1(){
        Order order = new Order();
        order.setId(1L);
        List<OrderItem> orderItems = orderItemJpaRepository.findByOrder(order);
        OrderItem orderItem = orderItems.get(0);
        Order resultOrder = orderItem.getOrder();
        Item item = orderItem.getItem();
        Member member = orderItem.getOrder().getMember();
    }
}