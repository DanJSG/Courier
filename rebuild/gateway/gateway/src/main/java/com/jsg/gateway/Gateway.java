package com.jsg.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class Gateway 
{
    public static void main( String[] args )
    {
		SpringApplication.run(Gateway.class, args);
        System.out.println("Gateway application running...");
    }
}
