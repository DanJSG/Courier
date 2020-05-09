package com.jsg.courier_old;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class CourierApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(CourierApplication.class, args);
		System.out.println("This application is now running...");
	}
}
