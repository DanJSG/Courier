package com.jsg.hive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Hive {
	
    public static void main(String[] args) {
    	SpringApplication.run(Hive.class, args);
        System.out.println("Hive running...");
    }
    
}
