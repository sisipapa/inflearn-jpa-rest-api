package com.example.inflearnjparestapi.api;

import com.example.inflearnjparestapi.repository.jpa.OrderItemJpaRepository;
import com.example.inflearnjparestapi.repository.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

}
