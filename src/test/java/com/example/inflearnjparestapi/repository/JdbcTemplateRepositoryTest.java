package com.example.inflearnjparestapi.repository;

import com.example.inflearnjparestapi.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JdbcTemplateRepositoryTest {

    @Autowired
    JdbcTemplateRepository jdbcTemplateRepository;

    @Test
    void findById() {
        jdbcTemplateRepository.findById(1L);
    }

    @Test
    void save(){
        Member member = new Member();
        member.setName("테스트1");
        jdbcTemplateRepository.save(member);
    }
}