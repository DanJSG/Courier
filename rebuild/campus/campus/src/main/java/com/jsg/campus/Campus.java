package com.jsg.campus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class Campus {
	
    public static void main( String[] args ) {
    	SpringApplication.run(Campus.class, args);
        System.out.println("Campus now open...");
    }
    
}
