package com.example.order_service;

import com.example.order_service.models.OrderEntity;
import com.example.order_service.models.OrderStatus;
import com.example.order_service.repositories.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(OrderRepository orderRepository) {
		return args -> {

//			OrderEntity order = new OrderEntity(1L, OrderStatus.PENDING);
//			orderRepository.save(order);
//			System.out.println(order);

			System.out.println("Order Server Running!");
		};
	}
}
