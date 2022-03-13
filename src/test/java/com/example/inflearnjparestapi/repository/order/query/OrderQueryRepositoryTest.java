package com.example.inflearnjparestapi.repository.order.query;

import com.example.inflearnjparestapi.domain.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.List;

@SpringBootTest
//@Import(InitDb.class)
class OrderQueryRepositoryTest {


    @PersistenceUnit
    private EntityManagerFactory factory;

    @Test
    void findOrders(){
        EntityManager em = factory.createEntityManager();
        em.createQuery(
                "select " +
                            "new com.example.inflearnjparestapi.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        "from Order o " +
                        "join o.member m " +
                        "join o.delivery d ", OrderQueryDto.class)
                .getResultList();
    }

    @Test
    void findAllWithMemberDelivery(){
        EntityManager em = factory.createEntityManager();
        em.createQuery(
                "select " +
                        "o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d ", Order.class)
                .getResultList();
    }

    @Test
    void findAllWithMemberDelivery2(){
        int offset = 0;
        int limit = 5;

        EntityManager em = factory.createEntityManager();
        List<Order> orders = em.createQuery(
                "select " +
                        "o " +
                        "from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        System.out.println(orders.size());
    }

    @Test
    void findAllWithItem(){
        int offset = 0;
        int limit = 10;

        // 1. join -> 6개
        // 2. distinct join -> 3개
        // 3. join fetch -> 3개
        // 3. distinct / join fetch -> 3개
        EntityManager em = factory.createEntityManager();
        List<Order> orders = em.createQuery(
                "select " +
                        "distinct o " +
                        "from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d " +
                        "join fetch o.orderItems oi " +
                        "join fetch oi.item i ", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        System.out.println("size : " + orders.size());
    }
}