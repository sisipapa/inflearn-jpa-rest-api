package com.example.inflearnjparestapi.api;

import com.example.inflearnjparestapi.domain.Address;
import com.example.inflearnjparestapi.domain.Order;
import com.example.inflearnjparestapi.domain.OrderItem;
import com.example.inflearnjparestapi.domain.OrderStatus;
import com.example.inflearnjparestapi.repository.OrderRepository;
import com.example.inflearnjparestapi.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * Entity에 JsonIgnore 설정을 안해서 현재는 오류발생 강의 내용만 참고하자!!
     *
     * @return
     */
    @GetMapping("/api/v1/orders")
    public List<Order> orderV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for(Order order: all){
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .distinct()
                .map(OrderDto::new)
                .collect(Collectors.toList());

        return result;
    }

    /**
     * ToOne(oneToOne, manyToOne)관계는 모두 fetch join 으로 조회
     * 컬렉션은 지연로딩으로 조회한다.
     * 지연로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size, @BatchSize를 적용
     *  hibernate.default_batch_fetch_size : Global설정
     *  @BatchSize 개별최적화
     *  이 옵션을 사용하면 컬렉션이나, 프록시 객체를 한꺼번에 설정한 size 만큼 IN 쿼리를 사용한다.
     * @return
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                       @RequestParam(value = "limit", defaultValue = "100") int limit){

        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());

        return result;
    }

    @Getter
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(OrderItemDto::new)
//                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());

//            order.getOrderItems().stream().forEach(o -> o.getItem().getName()); // Proxy 초기화를 해주지 않으면 orderItems는 null로 출력된다.
//            orderItems = order.getOrderItems();
        }
    }

    @Getter
    static class OrderItemDto{

        private String itemName;//상품명
        private int orderPrice; //주문가격
        private int count;      //주문수량

        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
