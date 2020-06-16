package com.larscheng.www;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@MapperScan("com.larscheng.www.dao")
public class NearbyMethodThreeApplication {
    public enum PizzaStatus {
        ORDERED,
        READY,
        DELIVERED;

    }
    public static void main(String[] args) {
//        boolean equals = PizzaStatus.ORDERED.equals(PizzaStatus.READY);
//        System.out.println(equals);
        SpringApplication.run(NearbyMethodThreeApplication.class, args);
    }

}
