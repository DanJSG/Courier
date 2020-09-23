package com.jsg.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class UserService {
	
    public static void main( String[] args ) {
    	SpringApplication.run(UserService.class, args);
        System.out.println("User service now running...");
    }
    
}
