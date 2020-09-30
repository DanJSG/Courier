package com.jsg.postie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;

import javax.swing.*;

@SpringBootApplication
public class Postie {

    public static void main(String[] args) {
        SpringApplication.run(Postie.class);
        System.out.println("Postie on its round...");
    }

}
