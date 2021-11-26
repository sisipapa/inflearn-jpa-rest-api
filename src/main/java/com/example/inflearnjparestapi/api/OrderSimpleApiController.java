package com.example.inflearnjparestapi.api;

import com.example.inflearnjparestapi.domain.Address;
import com.example.inflearnjparestapi.domain.Order;
import com.example.inflearnjparestapi.domain.OrderStatus;
import com.example.inflearnjparestapi.repository.OrderRepository;
import com.example.inflearnjparestapi.repository.OrderSearch;
import com.example.inflearnjparestapi.repository.simple.query.OrderSimpleQueryDto;
import com.example.inflearnjparestapi.repository.simple.query.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * 1. API의 응답결과가 List는 확장성이 떨어진다.
     * 2. 양방향 연관관계가 걸린 Entity들때문에 무한참조가 되어 에러가 발생
     * -> xToOne 관계의 Entity에 JsonIgnore를 걸어서 해결은 가능하지만 좋은 방법은 아니다.
     * 3. API의 요청파라미터(바디)나 응답결과로 Entity를 바로 사용하는 것은 좋지않다.
     * @return
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for(Order order: all){
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }

    /**
     * 엔티티를 DTO로 변환하는 일반적인 방법이다.
     * 쿼리가 총 1 + N + N번 실행된다. (v1과 쿼리수 결과는 같다.)
     *  order 조회 1번(order 조회 결과 수가 N이 된다.)
     *  order -> member 지연 로딩 조회 N 번
     *  order -> delivery 지연 로딩 조회 N 번
     *  예) order의 결과가 4개면 최악의 경우 1 + 4 + 4번 실행된다.(최악의 경우)
     *      지연로딩은 영속성 컨텍스트에서 조회하므로, 이미 조회된 경우 쿼리를 생략한다.
     * @return
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){

        // 1개의 쿼리가 수행되서 ORDER 2개의 ROW 리턴
        // N + 1 -> 1 + 회원N + 배송 N
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> dtoOrders = orders.stream()
                .map(SimpleOrderDto::new)
//                .map(order -> new SimpleOrderDto(order))
                .collect(Collectors.toList());

        return dtoOrders;
    }

    /**
     * 페치 조인으로 SQL이 1번만 실행됨
     * distinct 를 사용한 이유는 1대다 조인이 있으므로 데이터베이스 row가 증가한다. 그 결과 같은 order
     * 엔티티의 조회 수도 증가하게 된다. JPA의 distinct는 SQL에 distinct를 추가하고, 더해서 같은 엔티티가
     * 조회되면, 애플리케이션에서 중복을 걸러준다. 이 예에서 order가 컬렉션 페치 조인 때문에 중복 조회 되는
     * 것을 막아준다.
     * 단점
     * 페이징 불가능
     *
     * 참고: 컬렉션 페치 조인을 사용하면 페이징이 불가능하다. 하이버네이트는 경고 로그를 남기면서 모든
     * 데이터를 DB에서 읽어오고, 메모리에서 페이징 해버린다(매우 위험하다). 자세한 내용은 자바 ORM 표준
     * JPA 프로그래밍의 페치 조인 부분을 참고하자.
     *
     * 참고: 컬렉션 페치 조인은 1개만 사용할 수 있다. 컬렉션 둘 이상에 페치 조인을 사용하면 안된다. 데이터가
     * 부정합하게 조회될 수 있다. 자세한 내용은 자바 ORM 표준 JPA 프로그래밍을 참고하자
     * @return
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        // fetch join의 경우 LAZY로딩을 무시하고 쿼리시 한번에 조회
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());

        return result;
    }

    /**
     * 일반적인 SQL을 사용할 때 처럼 원하는 값을 선택해서 조회
     * new 명령어를 사용해서 JPQL의 결과를 DTO로 즉시 변환
     * SELECT 절에서 원하는 데이터를 직접 선택하므로 DB 애플리케이션 네트웍 용량 최적화(생각보다미비)
     * 리포지토리 재사용성 떨어짐, API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점
     * @return
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
}
