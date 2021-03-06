package com.example.inflearnjparestapi.repository.jpa;

import com.example.inflearnjparestapi.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"member", "delivery"}) // (2)
    List<Order> findAll();

    Optional<List<Order>> findByMember(Member member);

    Optional<Order> findByDeliveryAndMemberAndStatus(Delivery delivery, Member member, OrderStatus status);

}
