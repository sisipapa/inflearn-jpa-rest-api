package com.example.inflearnjparestapi.repository.querydsl;

import com.example.inflearnjparestapi.domain.Order;
import com.example.inflearnjparestapi.repository.OrderSearch;
import com.example.inflearnjparestapi.repository.order.query.OrderItemQueryDto;
import com.example.inflearnjparestapi.repository.order.query.OrderQueryDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.inflearnjparestapi.domain.QDelivery.delivery;
import static com.example.inflearnjparestapi.domain.QMember.member;
import static com.example.inflearnjparestapi.domain.QOrder.order;
import static com.example.inflearnjparestapi.domain.QOrderItem.orderItem;
import static com.example.inflearnjparestapi.domain.item.QItem.item;
import static java.util.stream.Collectors.groupingBy;
import static org.springframework.util.StringUtils.hasText;


/**
 * OrderRepository, OrderQueryRepository의 jpa 쿼리를 Querydsl로 변경한 Repository 기능은 동일하다.
 */
@Repository
@RequiredArgsConstructor
public class OrderQuerydslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Order> findAll(){
        List<Order> orders = jpaQueryFactory
                .selectFrom(order)
                .fetch();

        return orders;
    }

    public List<Order> findAllByString(OrderSearch orderSearch){

        BooleanBuilder builder = search(orderSearch);

        List<Order> orders = jpaQueryFactory
                .selectFrom(order)
                .where(builder)
                .fetch();

        return orders;
    }

    public List<Order> findAllWithMemberDelivery(){
        List<Order> orders = jpaQueryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .fetch();

        return orders;
    }

    public List<Order> findAllWithMemberDelivery(Integer offset, Integer limit) {
        List<Order> orders = jpaQueryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .offset(offset)
                .limit(limit)
                .fetch();

        return orders;
    }

    public List<Order> findAllWithItem(){
        List<Order> orders = jpaQueryFactory
                .selectFrom(order)
                .distinct()
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .join(order.orderItems, orderItem).fetchJoin()
                .join(orderItem.item, item).fetchJoin()
                .offset(0)
                .limit(5)
                .fetch();

        return orders;
    }

    /**
     * Query : 루트1번, 컬렉션 N번
     *
     * @return
     */
    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> orders = findOrders();

        orders.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return orders;
    }
    
    /**
     * Query : 루트1번, 컬렉션 1번
     *
     * @return
     */
    public List<OrderQueryDto> findAllByDto_optimization(){
        // 1. toOne fetch join
        List<OrderQueryDto> orders = findOrders();
        // 2. toMany 조회를 위한 키값 List
        List<Long> orderIds = findOrderIds(orders);
        // 3. 키값 List를 통해 toMany 데이터 조회
        List<OrderItemQueryDto> orderItmes = findOrderItems(orderIds);
        // 4. java groupingBy로 그룹핑
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItmes.stream()
                .collect(groupingBy(OrderItemQueryDto::getOrderId));
        // 5.그룹핑한 목록 forEach로 set
        orders.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return orders;
    }


    private List<Long> findOrderIds(List<OrderQueryDto> orders) {
        return orders.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
    }

    private List<OrderQueryDto> findOrders() {
        return jpaQueryFactory
                .select(
                        Projections.constructor(OrderQueryDto.class
                                , order.id
                                , member.name
                                , order.orderDate
                                , order.status
                                , delivery.address
                        )
                )
                .from(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .fetch();
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(OrderItemQueryDto.class
                        , orderItem.order.id
                        , item.name
                        , orderItem.orderPrice
                        , orderItem.count)
                )
                .from(orderItem)
                .join(orderItem.item, item)
                .where(orderItem.order.id.eq(orderId))
                .fetch();
    }

    private List<OrderItemQueryDto> findOrderItems(List<Long> orderIds) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(OrderItemQueryDto.class
                                , orderItem.order.id
                                , item.name
                                , orderItem.orderPrice
                                , orderItem.count)
                )
                .from(orderItem)
                .join(orderItem.item, item)
                .where(orderItem.order.id.in(orderIds))
                .fetch();
    }

    private BooleanBuilder search(OrderSearch orderSearch) {

        BooleanBuilder builder = new BooleanBuilder();

        // OrderStatus eq
        if(Objects.nonNull(orderSearch.getOrderStatus())){
            builder.and(order.status.eq(orderSearch.getOrderStatus()));
        }

        if(hasText(orderSearch.getMemberName())){
            builder.and(order.member.name.eq(orderSearch.getMemberName()));
        }

        return builder;

    }

}
