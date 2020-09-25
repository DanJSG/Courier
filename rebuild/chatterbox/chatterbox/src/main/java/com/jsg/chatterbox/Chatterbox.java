package com.jsg.chatterbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class Chatterbox {
	
    public static void main(String[] args) {
    	SpringApplication.run(Chatterbox.class, args);
        System.out.println("Chatterbox running...");
    }
    
}
