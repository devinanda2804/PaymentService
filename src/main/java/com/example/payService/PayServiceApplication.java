package com.example.payService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.payService.feign")
public class PayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayServiceApplication.class, args);
	}

}
