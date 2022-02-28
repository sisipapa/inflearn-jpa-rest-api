package com.example.inflearnjparestapi.repository.jpa;

import com.example.inflearnjparestapi.InitDb;
import com.example.inflearnjparestapi.domain.Delivery;
import com.example.inflearnjparestapi.domain.Member;
import com.example.inflearnjparestapi.domain.Order;
import com.example.inflearnjparestapi.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@Import(InitDb.class)
class OrderJpaRepositoryTest {

    @Autowired
    OrderJpaRepository orderJpaRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void test1(){
        List<Order> orders = orderJpaRepository.findAll();
        assertThat(orders.size(), is(2));

    }

    @Test
    @DisplayName("파라미터(delevery,member,status) 조회")
    void findByDeliveryAndMemberAndStatus(){
        Delivery delivery = new Delivery();
        delivery.setId(1L);

        Member member = new Member();
        member.setId(1L);

        Order order = orderJpaRepository.findByDeliveryAndMemberAndStatus(delivery, member, OrderStatus.ORDER).orElse(null);
        assertThat(order.getMember().getName(), is("userA"));
    }

    @Test
    void test3(){
        Member member = new Member();
        member.setId(3L);
        List<Order> orders = orderJpaRepository.findByMember(member).orElseGet(ArrayList<Order>::new);
        assertThat(orders.size(), is(0));
    }

}